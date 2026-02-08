package com.itcotato.naengjango.domain.igloo.service;

import com.itcotato.naengjango.domain.igloo.exception.IglooException;
import com.itcotato.naengjango.domain.igloo.exception.code.IglooErrorCode;
import com.itcotato.naengjango.domain.igloo.dto.IglooResponseDto;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.member.repository.MemberRepository;
import com.itcotato.naengjango.domain.snowball.service.SnowballService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IglooService {

    // 업그레이드 비용
    private static final int[] UPGRADE_COST = {0, 5, 10, 15, 20}; // index: next level (2..5)
    // 실패 5회마다 하락 판정
    private static final int FAIL_THRESHOLD = 5;
    // 하락 방어 비용(확정: 8개)
    private static final int DOWNGRADE_PROTECT_COST = 8;

    private final MemberRepository memberRepository;
    private final SnowballService snowballService;

    @Transactional(readOnly = true)
    public IglooResponseDto.Status getStatus(Member member) {
        int balance = snowballService.getBalance(member);
        Integer required = requiredForNext(member.getIglooLevel());
        return new IglooResponseDto.Status(
                member.getIglooLevel(),
                balance,
                required,
                member.getFreezeFailCount()
        );
    }

    /**
     * 수동 업그레이드: 눈덩이 소비됨
     */
    @Transactional
    public IglooResponseDto.UpgradeResult upgrade(Member member) {
        Member m = memberRepository.findById(member.getId()).orElseThrow();

        if (m.isMaxIglooLevel()) {
            // max면 예외처리
            throw new IglooException(IglooErrorCode.IGLOO_LEVEL_MAX);
        }

        int before = m.getIglooLevel();
        int nextLevel = before + 1;
        int cost = upgradeCost(nextLevel);

        int balance = snowballService.getBalance(m);
        if (balance < cost) {
            throw new IglooException(IglooErrorCode.SNOWBALL_INSUFFICIENT);
        }

        // 소비 → 업그레이드
        snowballService.spend(m, cost, SnowballService.REASON_IGLOO_UPGRADE);
        m.upIglooLevel();

        int afterBalance = snowballService.getBalance(m);

        return new IglooResponseDto.UpgradeResult(
                before,
                m.getIglooLevel(),
                afterBalance,
                cost
        );
    }

    /**
     * 실패 누적 반영 + 임계 도달 시 하락/방어 처리
     * - 방어 가능(눈덩이 8개 이상) → 8개 소비 후 방어
     * - 불가능 → 단계 하락(최소 1)
     * - 처리 후 실패 누적 카운트는 0으로 초기화
     */
    @Transactional
    public void applyFailures(Member member, int failDelta) {
        if (failDelta <= 0) return;

        Member m = memberRepository.findById(member.getId()).orElseThrow();

        m.addFreezeFailCount(failDelta);

        if (!m.reachedFailThreshold(FAIL_THRESHOLD)) {
            return;
        }

        // 임계 도달: 방어 가능 여부
        int balance = snowballService.getBalance(m);

        if (balance >= DOWNGRADE_PROTECT_COST) {
            // 방어: 눈덩이 8개 소비
            snowballService.spend(m, DOWNGRADE_PROTECT_COST, SnowballService.REASON_IGLOO_DOWN_PROTECT);
            m.resetFreezeFailCount();
            return;
        }

        // 방어 불가: 단계 하락
        m.downIglooLevel();
        m.resetFreezeFailCount();
    }

    private int upgradeCost(int nextLevel) {
        if (nextLevel < 2 || nextLevel > 5) throw new IllegalArgumentException("invalid level");
        return UPGRADE_COST[nextLevel - 1]; // nextLevel=2 -> index1(5)
    }

    private Integer requiredForNext(int currentLevel) {
        if (currentLevel >= 5) return null;
        int next = currentLevel + 1;
        return upgradeCost(next);
    }
}

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

        return buildStatus(member);
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
     * 실패 누적 체크: 실패 누적 delta만큼 더한 후 임계 도달 여부 체크
     * - 임계 도달 안함: 실패 누적 업데이트 후 false 반환
     * - 임계 도달: 눈덩이 잔액 체크 후 방어 가능 여부 반환 (방어 시 실패 누적 초기화)
     * - 하락 확정은 여기서 하지 않음(별도 downgrade() 호출로 처리)
     */
    @Transactional
    public IglooResponseDto.FailCheckResult checkFailures(Member member, int failDelta) {

        if (failDelta <= 0) {
            return new IglooResponseDto.FailCheckResult(false, false, member.getIglooLevel());
        }

        Member m = memberRepository.findById(member.getId()).orElseThrow();

        // 1레벨이면 실패 누적 x
        if (m.getIglooLevel() <= 1) {
            return new IglooResponseDto.FailCheckResult(
                    false,
                    false,
                    m.getIglooLevel()
            );
        }

        m.addFreezeFailCount(failDelta);

        if (!m.reachedFailThreshold(FAIL_THRESHOLD)) {
            return new IglooResponseDto.FailCheckResult(false, false, m.getIglooLevel());
        }


        int balance = snowballService.getBalance(m);
        boolean canProtect = balance >= DOWNGRADE_PROTECT_COST;

        return new IglooResponseDto.FailCheckResult(true, canProtect, m.getIglooLevel());
    }

    /**
     * 하락 방어: 실패 누적 임계 도달 상태에서 별도 호출됨
     * - 눈덩이 소비(확정: 8개)
     * - 실패 누적 카운트 초기화
     * - 이글루 단계 유지
     */
    @Transactional
    public IglooResponseDto.Status protect(Member member) {

        Member m = memberRepository.findById(member.getId()).orElseThrow();

        if (!m.reachedFailThreshold(FAIL_THRESHOLD)) {
            throw new IglooException(IglooErrorCode.INVALID_PROTECT_REQUEST);
        }

        int balance = snowballService.getBalance(m);

        if (balance < DOWNGRADE_PROTECT_COST) {
            throw new IglooException(IglooErrorCode.SNOWBALL_INSUFFICIENT);
        }

        snowballService.spend(
                m,
                DOWNGRADE_PROTECT_COST,
                SnowballService.REASON_IGLOO_DOWN_PROTECT
        );

        m.resetFreezeFailCount();

        return buildStatus(m);
    }


    /**
     * 하락 확정: 실패 누적 임계 도달 상태에서 별도 호출됨
     * - 하락 처리(최소 1)
     * - 실패 누적 카운트 초기화
     */
    @Transactional
    public IglooResponseDto.Status downgrade(Member member) {

        Member m = memberRepository.findById(member.getId()).orElseThrow();

        // 임계 도달 여부 체크(임계 도달 안된 상태에서 호출되면 예외)
        if (!m.reachedFailThreshold(FAIL_THRESHOLD)) {
            throw new IglooException(IglooErrorCode.NOT_REACHED_THRESHOLD);
        }

        // 이미 레벨 1인 경우 예외처리
        if (m.getIglooLevel() <= 1) {
            throw new IglooException(IglooErrorCode.IGLOO_LEVEL_MIN);
        }

        m.downIglooLevel();
        m.resetFreezeFailCount();

        return buildStatus(m);
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

    private IglooResponseDto.Status buildStatus(Member m) {
        return new IglooResponseDto.Status(
                m.getIglooLevel(),
                snowballService.getBalance(m),
                requiredForNext(m.getIglooLevel()),
                m.getFreezeFailCount()
        );
    }

}

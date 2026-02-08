package com.itcotato.naengjango.domain.snowball.service;

import com.itcotato.naengjango.domain.member.dto.SnowballResponseDto;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.snowball.entity.SnowballLedger;
import com.itcotato.naengjango.domain.snowball.repository.SnowballLedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SnowballService {

    public static final String REASON_FREEZE_SUCCESS = "FREEZE_SUCCESS";
    public static final String REASON_FREEZE_STREAK_3_DAYS = "FREEZE_STREAK_3_DAYS";
    public static final String REASON_IGLOO_UPGRADE = "IGLOO_UPGRADE";
    public static final String REASON_IGLOO_DOWN_PROTECT = "IGLOO_DOWN_PROTECT";

    private final SnowballLedgerRepository snowballLedgerRepository;

    @Transactional(readOnly = true)
    public int getBalance(Member member) {
        return snowballLedgerRepository.sumBalance(member);
    }

    @Transactional(readOnly = true)
    public int getTodayBasicEarned(Member member) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        // 기본 지급은 reason=FREEZE_SUCCESS로만 제한
        return snowballLedgerRepository.sumByReasonInRange(member, REASON_FREEZE_SUCCESS, start, end);
    }

    @Transactional
    public void earn(Member member, int amount, String reason) {
        snowballLedgerRepository.save(SnowballLedger.earn(member, amount, reason));
    }

    @Transactional
    public void spend(Member member, int amount, String reason) {
        snowballLedgerRepository.save(SnowballLedger.spend(member, amount, reason));
    }

    @Transactional(readOnly = true)
    public SnowballResponseDto.Summary getSummary(Member member) {

        int total = snowballLedgerRepository.sumBalance(member);

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        int todayEarned =
                snowballLedgerRepository.sumTodayEarned(member, start, end);

        return new SnowballResponseDto.Summary(
                total,
                todayEarned
        );
    }

    @Transactional(readOnly = true)
    public Page<SnowballResponseDto.History> getHistory(
            Member member,
            Pageable pageable
    ) {
        return snowballLedgerRepository
                .findByMemberOrderByCreatedAtDesc(member, pageable)
                .map(l -> new SnowballResponseDto.History(
                        l.getAmount(),
                        l.getReason(),
                        l.getCreatedAt()
                ));
    }
}

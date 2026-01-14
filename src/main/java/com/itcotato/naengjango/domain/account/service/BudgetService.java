package com.itcotato.naengjango.domain.account.service;

import com.itcotato.naengjango.domain.account.dto.BudgetResponseDTO;
import com.itcotato.naengjango.domain.account.exception.code.AccountErrorCode;
import com.itcotato.naengjango.domain.account.repository.TransactionRepository;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.member.exception.code.MemberErrorCode;
import com.itcotato.naengjango.domain.member.repository.MemberRepository;
import com.itcotato.naengjango.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 남은 예산 정보(오늘 예산/이번달 예산) 조회 관련 서비스
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BudgetService {
    private final TransactionRepository transactionRepository;
    private final MemberRepository memberRepository;

    public BudgetResponseDTO.BudgetStatusDTO getBudgetStatus(Long memberId, int year, int month, int day) {
        // 1. 회원 존재 여부 및 조회 권한 확인
        if (memberId == null) {
            throw new GeneralException(AccountErrorCode.ACCOUNT_FORBIDDEN);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));

        try {
            // 2. 시간 범위 설정
            LocalDate targetDate = LocalDate.of(year, month, day);
            LocalDateTime startOfDay = targetDate.atStartOfDay();
            LocalDateTime endOfDay = targetDate.atTime(23, 59, 59);
            LocalDateTime startOfMonth = targetDate.withDayOfMonth(1).atStartOfDay();

            // 3. 지출액 조회
            // Transaction에서 'EXPENSE'의 합계
            Long todaySum = transactionRepository.sumExpenseByMemberAndDate(memberId, startOfDay, endOfDay);
            long todayExpenditure = (todaySum != null) ? todaySum : 0L;

            Long monthSum = transactionRepository.sumExpenseByMemberAndDate(memberId, startOfMonth, endOfDay);
            long monthExpenditure = (monthSum != null) ? monthSum : 0L;

            // 4. 유동적으로 예산 계산
            int totalBudget = (member.getBudget() != null) ? member.getBudget() : 0;
            int daysInMonth = targetDate.lengthOfMonth();
            int remainingDays = daysInMonth - targetDate.getDayOfMonth() + 1; // 오늘 포함 남은 일수

            // 어제까지의 누적 지출액 계산
            long expenditureBeforeToday = monthExpenditure - todayExpenditure;
            // 남은 한 달 예산
            long remainingBudgetForMonth = totalBudget - expenditureBeforeToday;

            // 오늘 권장 예산 = (남은 한달 예산 / 오늘 포함 남은 일수)
            int dynamicDailyBudget = (remainingBudgetForMonth > 0)
                    ? (int) (remainingBudgetForMonth / remainingDays)
                    : 0;

            // 5. 오늘 남은 예산 계산 (소진 시 0원)
            int todayRemaining = Math.max(0, dynamicDailyBudget - (int) todayExpenditure);

            // 6. 이번 달 남은 예산 계산 (소진 시 0원)
            int monthRemaining = Math.max(0, totalBudget - (int) monthExpenditure);

            return BudgetResponseDTO.BudgetStatusDTO.builder()
                    .todayRemaining(todayRemaining)
                    .monthRemaining(monthRemaining)
                    .build();

        } catch (Exception e) {
            throw new GeneralException(AccountErrorCode.ACCOUNT_SERVER_ERROR);
        }
    }
}
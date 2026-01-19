package com.itcotato.naengjango.domain.report.service;

import com.itcotato.naengjango.domain.account.repository.TransactionRepository;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.member.repository.MemberRepository;
import com.itcotato.naengjango.domain.report.dto.ReportResponseDTO;
import com.itcotato.naengjango.domain.report.exception.code.ReportErrorCode;
import com.itcotato.naengjango.domain.member.exception.code.MemberErrorCode;
import com.itcotato.naengjango.domain.report.repository.ReportRepository;
import com.itcotato.naengjango.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {
    private final ReportRepository reportRepository;
    private final TransactionRepository transactionRepository;
    private final MemberRepository memberRepository;

    public ReportResponseDTO.DailyBudgetReportDTO getDailyBudgetReport(Long memberId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        // 1. 회원 정보 조회 및 예산(Budget) 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));

        Long monthlyBudget = (member.getBudget() != null) ? member.getBudget().longValue() : 0L;
        if (monthlyBudget == 0) {
            throw new GeneralException(ReportErrorCode.BUDGET_NOT_FOUND);
        }

        // 2. 이번 달 총 누적 지출액 조회 (파산 예측의 기준이 됨)
        Long totalSpentValue = transactionRepository.sumExpenseByMemberAndDate(memberId, startOfMonth, now);
        Long totalSpent = (totalSpentValue != null) ? totalSpentValue : 0L;

        // 3. 최근 8일(7일 전~ 오늘) 가용 예산 추이 계산
        List<ReportResponseDTO.DailyTrendDTO> availableTrends = IntStream.rangeClosed(0, 7)
                .mapToObj(i -> {
                    LocalDate targetDate = today.minusDays(7 - i);
                    LocalDateTime targetEnd = targetDate.atTime(23, 59, 59);

                    // 해당 날짜까지의 누적 지출액
                    Long spentUntilThen = transactionRepository.sumExpenseByMemberAndDate(memberId, startOfMonth, targetEnd);
                    if (spentUntilThen == null) spentUntilThen = 0L;

                    // 해당 날짜 기준 남은 일수 및 가용 예산 계산
                    long remainingDaysFromThen = (long) targetDate.lengthOfMonth() - targetDate.getDayOfMonth() + 1;
                    Long availableThen = Math.max(0L, (monthlyBudget - spentUntilThen) / remainingDaysFromThen);

                    return ReportResponseDTO.DailyTrendDTO.builder()
                            .date(targetDate.toString())
                            .amount(availableThen)
                            .build();
                })
                .toList();

//        // 4. 오늘 가용 예산
//        Long todayAvailable = availableTrends.get(availableTrends.size() - 1).getAmount();

        // 4. [추가] 오늘 가용 예산 및 어제 대비 증감 수치 계산
        // availableTrends의 마지막 인덱스(7)는 오늘, 그 앞(6)은 어제입니다.
        Long todayAvailable = availableTrends.get(7).getAmount();
        Long yesterdayAvailable = availableTrends.get(6).getAmount();
        Long diffFromYesterday = todayAvailable - yesterdayAvailable;

        // 5. 파산 예측을 위한 지출 데이터 조회
        List<Object[]> results = reportRepository.findDailyTrendsRaw(memberId, today.minusDays(7).atStartOfDay());
        Map<String, Long> expenseMap = results.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> ((Number) row[1]).longValue(),
                        (existing, replacement) -> existing
                ));

        // 6. 8일치 지출 리스트 생성
        List<ReportResponseDTO.DailyTrendDTO> actualExpenses = IntStream.rangeClosed(0, 7)
                .mapToObj(i -> {
                    String dateStr = today.minusDays(7 - i).toString();
                    return ReportResponseDTO.DailyTrendDTO.builder()
                            .date(dateStr)
                            .amount(expenseMap.getOrDefault(dateStr, 0L))
                            .build();
                })
                .toList();

        // 7. 지출 기반 파산 시나리오 예측
        List<ReportResponseDTO.BankruptcyDTO> bankruptcyPrediction = IntStream.range(0, actualExpenses.size())
                .mapToObj(i -> {
                    // 해당 시점까지의 지출 평균을 계산
                    double rollingAvg = actualExpenses.subList(0, i + 1).stream()
                            .mapToLong(ReportResponseDTO.DailyTrendDTO::getAmount)
                            .average()
                            .orElse(0.0);

                    // 현재 남은 잔액를 기준으로 예측
                    String expectedDate = calculateBankruptcyDate(today, monthlyBudget - totalSpent, rollingAvg);

                    return ReportResponseDTO.BankruptcyDTO.builder()
                            .baseDate(actualExpenses.get(i).getDate())
                            .expectedDate(expectedDate)
                            .build();
                })
                .toList();

        // 8. 최종 반환 (diffFromYesterday 포함)
        return ReportResponseDTO.DailyBudgetReportDTO.builder()
                .todayAvailable(todayAvailable)
                .diffFromYesterday(diffFromYesterday)
                .dailyTrends(availableTrends)
                .bankruptcyPrediction(bankruptcyPrediction)
                .build();
    }

    // 파산 시나리오 계산하는 메서드
    private String calculateBankruptcyDate(LocalDate today, Long balance, double avgSpent) {
        // 이번 달의 마지막 날 미리 구해놓기
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        // 지출이 없거나(0) 마이너스인 경우, 파산하지 않는 것으로 간주하여 월말 날짜 반환
        if (avgSpent <= 0) {
            return lastDayOfMonth.toString();
        }

        long daysToBankruptcy = (long) (balance / avgSpent);
        LocalDate expectedDate = today.plusDays(daysToBankruptcy);

        if (expectedDate.isAfter(lastDayOfMonth)) {
            return lastDayOfMonth.toString();
        }
        return expectedDate.toString();
    }
}

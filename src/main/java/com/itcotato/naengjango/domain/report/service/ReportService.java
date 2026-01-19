package com.itcotato.naengjango.domain.report.service;

import com.itcotato.naengjango.domain.account.repository.TransactionRepository;
import com.itcotato.naengjango.domain.freeze.entity.FreezeItem;
import com.itcotato.naengjango.domain.freeze.enums.FreezeStatus;
import com.itcotato.naengjango.domain.freeze.repository.FreezeItemRepository;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
    private final FreezeItemRepository freezeItemRepository;

    /**
     * 하루 가용 예산 및 파산 시나리오 관련 서비스 코드
     */
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

        // 4. 오늘 가용 예산 및 어제 대비 증감 수치 계산
        Long todayAvailable = availableTrends.get(7).getAmount(); // 오늘
        Long yesterdayAvailable = availableTrends.get(6).getAmount(); // 어제
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

    /**
     * 냉동 절약 효과(주간/월간) 관련 서비스 코드
     */

    public ReportResponseDTO.SavingsEffectDTO getSavingsEffect(Long memberId, String period) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // 1. 해당 기간(주/월) 냉동 성공 금액 계산
        LocalDateTime startOfPeriod = period.equals("month") ?
                today.withDayOfMonth(1).atStartOfDay() :
                today.minusDays(today.getDayOfWeek().getValue() - 1).atStartOfDay();

        Long totalSavedAmount = freezeItemRepository.sumPriceByMemberAndStatus(
                memberId, startOfPeriod, now, FreezeStatus.SUCCESS);
        if (totalSavedAmount == null) totalSavedAmount = 0L;

        // 차액 비교를 위해 지난 기간(주/월) 성공 금액 계산
        LocalDateTime startOfLastPeriod;
        LocalDateTime endOfLastPeriod;

        if (period.equals("month")) {
            startOfLastPeriod = today.minusMonths(1).withDayOfMonth(1).atStartOfDay();
            endOfLastPeriod = startOfPeriod.minusNanos(1); // 저번 달 마지막 순간
        } else {
            startOfLastPeriod = startOfPeriod.minusWeeks(1); // 저번 주 월요일
            endOfLastPeriod = startOfPeriod.minusNanos(1); // 저번 주 일요일 마지막 순간
        }

        Long lastPeriodSavedAmount = freezeItemRepository.sumPriceByMemberAndStatus(
                memberId, startOfLastPeriod, endOfLastPeriod, FreezeStatus.SUCCESS);
        lastPeriodSavedAmount = (lastPeriodSavedAmount != null) ? lastPeriodSavedAmount : 0L;

        // 지난 기간 대비 차액 (양수면 이번에 더 아낌, 음수면 저번에 더 아낌)
        Long diffFromLastPeriod = totalSavedAmount - lastPeriodSavedAmount;


        // 2. 성공률 추이 계산
        List<ReportResponseDTO.TrendDataDTO> successTrends = period.equals("month") ?
                getMonthlySuccessTrends(memberId, today) : getWeeklySuccessTrends(memberId, today);

        // 3. 요일별 성공률 계산 (최근 4주 또는 4개월 데이터)
        LocalDateTime startForAnalysis = today.minusMonths(1).atStartOfDay();
        List<FreezeItem> itemsForAnalysis = freezeItemRepository.findAllByMemberIdAndFrozenAtBetween(memberId, startForAnalysis, now);

        Map<String, Double> successRateByDay = getSuccessRateByDayOfWeek(itemsForAnalysis);
        ReportResponseDTO.BestSavingTimeDTO bestSavingTime = calculateBestSavingTime(itemsForAnalysis);

        return ReportResponseDTO.SavingsEffectDTO.builder()
                .totalSavedAmount(totalSavedAmount)
                .diffFromLastWeek(diffFromLastPeriod)
                .successTrends(successTrends)
                .successRateByDay(successRateByDay)
                .bestSavingTime(bestSavingTime)
                .build();
    }

    /**
     * 파산 시나리오 계산하는 메서드
     */

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

    /**
     * 최근 4주간 주차별 냉동 성공률 계산하는 메서드
     */
    private List<ReportResponseDTO.TrendDataDTO> getWeeklySuccessTrends(Long memberId, LocalDate today) {
        return IntStream.rangeClosed(0, 4)
                .mapToObj(i -> {
                    LocalDate start = today.minusWeeks(4 - i).with(DayOfWeek.MONDAY);
                    LocalDate end = start.plusDays(6);
                    double rate = calculateSuccessRate(memberId, start.atStartOfDay(), end.atTime(23, 59, 59));
                    return new ReportResponseDTO.TrendDataDTO((4 - i) == 0 ? "이번 주" : (4 - i) + "주 전", rate);
                }).toList();
    }

    /**
     * 최근 4개월간의 월별 냉동 성공률 계산하는 메서드
     */
    private List<ReportResponseDTO.TrendDataDTO> getMonthlySuccessTrends(Long memberId, LocalDate today) {
        return IntStream.rangeClosed(0, 4)
                .mapToObj(i -> {
                    LocalDate targetMonth = today.minusMonths(4 - i);
                    double rate = calculateSuccessRate(memberId, targetMonth.withDayOfMonth(1).atStartOfDay(),
                            targetMonth.withDayOfMonth(targetMonth.lengthOfMonth()).atTime(23, 59, 59));
                    return new ReportResponseDTO.TrendDataDTO(targetMonth.getMonthValue() + "월", rate);
                }).toList();
    }

    /**
     * 냉동 성공률 계산 (성공 개수 / 전체 개수)
     */
    private double calculateSuccessRate(Long memberId, LocalDateTime start, LocalDateTime end) {
        long totalCount = freezeItemRepository.countByMemberIdAndFrozenAtBetween(memberId, start, end);

        // 분모가 0이면 계산 불가이므로 0.0 반환
        if (totalCount == 0) return 0.0;

        long successCount = freezeItemRepository.countByMemberIdAndStatusAndFrozenAtBetween(
                memberId, FreezeStatus.SUCCESS, start, end);

        // 소수점 결과를 위해 double로 캐스팅해 반환
        return (double) successCount / totalCount;
    }

    /**
     * 요일별 성공률 히트맵 데이터 계산
     */
    private Map<String, Double> getSuccessRateByDayOfWeek(List<FreezeItem> items) {
        Map<String, Double> dayRates = new LinkedHashMap<>();

        for (DayOfWeek day : DayOfWeek.values()) {
            // 요일 이름
            String dayName = day.getDisplayName(TextStyle.NARROW, Locale.KOREAN);

            // 해당 요일의 데이터만 필터링
            List<FreezeItem> itemsOnDay = items.stream()
                    .filter(item -> item.getFrozenAt().getDayOfWeek() == day)
                    .toList();

            if (itemsOnDay.isEmpty()) {
                dayRates.put(dayName, 0.0);
                continue;
            }

            // 성공률 계산
            long successCount = itemsOnDay.stream()
                    .filter(item -> item.getStatus() == FreezeStatus.SUCCESS)
                    .count();

            double rate = (double) successCount / itemsOnDay.size();
            dayRates.put(dayName, rate);
        }
        return dayRates;
    }


    /**
     * 전체 데이터 리스트를 받아 가장 성공률이 높은 요일과 시간대를 계산합니다.
     */
    private ReportResponseDTO.BestSavingTimeDTO calculateBestSavingTime(List<FreezeItem> items) {
        String bestDay = "데이터 없음";
        String bestSlot = "-";
        double maxRate = -1.0;

        for (DayOfWeek day : DayOfWeek.values()) {
            for (String slot : List.of("오전", "오후")) {
                // 특정 요일 및 시간대(오전/오후) 필터링
                List<FreezeItem> filtered = items.stream()
                        .filter(item -> item.getFrozenAt().getDayOfWeek() == day)
                        .filter(item -> {
                            int hour = item.getFrozenAt().getHour();
                            return slot.equals("오전") ? hour < 12 : hour >= 12;
                        }).toList();

                if (filtered.isEmpty()) continue;

                // 성공률 계산
                long successCount = filtered.stream()
                        .filter(item -> item.getStatus() == FreezeStatus.SUCCESS).count();
                double rate = (double) successCount / filtered.size();

                // 최댓값 갱신
                if (rate > maxRate) {
                    maxRate = rate; // 여기서도 Math.round 부분을 제거!
                    bestDay = day.getDisplayName(TextStyle.FULL, Locale.KOREAN);
                    bestSlot = slot;
                }
            }
        }

        return ReportResponseDTO.BestSavingTimeDTO.builder()
                .day(bestDay)
                .timeSlot(bestSlot)
                .successRate(maxRate == -1.0 ? 0.0 : maxRate)
                .build();
    }
}

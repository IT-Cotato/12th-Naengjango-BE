package com.itcotato.naengjango.domain.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 분석 리포트 관련 응답 DTO 클래스입니다.
 */

public class ReportResponseDTO {

    /**
     * 하루 가용 예산 및 파산 시나리오 통합 응답 DTO
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "하루 가용 예산 및 파산 시나리오 응답 정보")
    public static class DailyBudgetReportDTO {
        @Schema(description = "오늘 사용할 수 있는 가용 예산", example = "25000")
        private Long todayAvailable;

        @Schema(description = "어제 가용 예산 대비 오늘의 증감 수치", example = "700")
        private Long diffFromYesterday;

        @Schema(description = "최근 8일간(오늘 포함) 가용 예산 추이 리스트")
        private List<DailyTrendDTO> dailyTrends;

        @Schema(description = "현재 소비 패턴 기준 파산 시나리오 예측 정보")
        private List<BankruptcyDTO> bankruptcyPrediction;
    }

    /**
     * 일별 예산/지출 정보를 담는 DTO
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "일별 지출 추이 정보")
    public static class DailyTrendDTO {
        @Schema(description = "분석 대상 날짜", example = "2026-01-19")
        private String date;

        @Schema(description = "해당 날짜의 지출액", example = "12000")
        private Long amount;
    }

    /**
     * 날짜별 파산 예측 정보를 담는 DTO
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "파산 시나리오 예측 정보")
    public static class BankruptcyDTO {

        @Schema(description = "분석 기준 날짜", example = "2026-01-19")
        private String baseDate;

        @Schema(description = "파산 예정일", example = "2026-01-25")
        private String expectedDate;
    }

    /**
     * 냉동 절약 효과(냉동으로 지킨 금액) 정보를 담는 DTO
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "냉동 절약 효과 리포트 응답 정보")
    public static class SavingsEffectDTO {

        @Schema(description = "이번 주/달 냉동 성공으로 지킨 총 금액", example = "89000")
        private Long totalSavedAmount;

        @Schema(description = "저번 주 대비 추가 절약 금액 (양수면 더 아낌, 음수면 덜 아낌)", example = "5000")
//        private Long diffFromLastWeek;
        private Long diffFromLastPeriod;

        @Schema(description = "이번 주/달 냉동 실패로 놓친 총 금액", example = "12000")
        private Long totalFailedAmount;

        @Schema(description = "저번 기간 대비 실패 금액 증감 (양수면 더 낭비함, 음수면 덜 낭비함)", example = "-2000")
        private Long diffFailedFromLastPeriod;

        @Schema(description = "성공률 추이 리스트", example = "[{\"label\": \"1주 전\", \"successRate\": 0.75}]")
        private List<TrendDataDTO> successTrends;

        @Schema(description = "요일별 냉동 성공률 (Key: 요일명, Value: 성공률 0.0~1.0)",
                example = "{\"MONDAY\": 0.85, \"TUESDAY\": 0.4}")
        private Map<String, Double> successRateByDay;

        @Schema(description = "가장 절약 성공률이 높은 요일 및 시간대 정보")
        private BestSavingTimeDTO bestSavingTime;
    }

    /**
     * 기간(주간/월간)별 냉동 성공률 추이 정보를 담는 DTO
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "기간별 성공률 추이 데이터")
    public static class TrendDataDTO {

        @Schema(description = "분석 단위 라벨(주간: 'n주 전', 월간: 'n월')", example = "1주 전")
        private String label;

        @Schema(description = "해당 기간의 냉동 성공률 (0.0 ~ 1.0)", example = "0.75")
        private Double successRate;
    }

    /**
     * 가장 성공률이 높은 요일 및 시간대 정보를 담는 DTO
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "냉동 성공률이 높은 요일과 시간대 정보")
    public static class BestSavingTimeDTO {
        @Schema(description = "성공률이 가장 높은 요일", example = "금요일")
        private String day;

        @Schema(description = "성공률이 가장 높은 시간대 (오전/오후)", example = "오후")
        private String timeSlot;

        @Schema(description = "해당 시점의 성공률 (0.0 ~ 1.0)", example = "1.0")
        private Double successRate;
    }
}

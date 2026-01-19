package com.itcotato.naengjango.domain.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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
        private Long todayAvailable; // 오늘 가용 예산

        @Schema(description = "어제 가용 예산 대비 오늘의 증감 수치", example = "700")
        private Long diffFromYesterday;

        @Schema(description = "최근 8일간(오늘 포함) 가용 예산 추이 리스트")
        private List<DailyTrendDTO> dailyTrends; // 최근 8일간(오늘 포함) 가용 예산

        @Schema(description = "현재 소비 패턴 기준 파산 시나리오 예측 정보")
        private List<BankruptcyDTO> bankruptcyPrediction; // 파산 시나리오
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
        private String date; // 분석 대상 날짜(yyyy-MM-dd)

        @Schema(description = "해당 날짜의 지출액", example = "12000")
        private Long amount; // 지출액
    }

    /**
     * 날짜별 파산 예측 정보를 담는 DTO
     */
    @Builder
    @Getter
    @NoArgsConstructor // 기본 생성자 필수
    @AllArgsConstructor
    @Schema(description = "파산 시나리오 예측 정보")
    public static class BankruptcyDTO {
        @Schema(description = "오늘 가용 예산", example = "10000")
        private Long todayAvailable;

        @Schema(description = "분석 기준 날짜", example = "2026-01-19")
        private String baseDate;     // 분석 기준일

        @Schema(description = "예산 소진 예정일", example = "2026-01-25")
        private String expectedDate; // 파산 예정일
    }
}

package com.itcotato.naengjango.domain.report.dto;

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
    public static class DailyBudgetReportDTO {
        private Long todayAvailable; // 오늘 가용 예산
        private List<DailyTrendDTO> dailyTrends; // 최근 8일간(오늘 포함) 가용 예산
        private List<BankruptcyDTO> bankruptcyPrediction; // 파산 시나리오
    }

    /**
     * 일별 예산/지출 정보를 담는 DTO
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyTrendDTO {
        private String date; // 분석 대상 날짜(yyyy-MM-dd)
        private Long amount; // 지출액
    }

    /**
     * 날짜별 파산 예측 정보를 담는 DTO
     */
    @Builder
    @Getter
    @NoArgsConstructor // 기본 생성자 필수
    @AllArgsConstructor
    public static class BankruptcyDTO {
        private String baseDate;     // 분석 기준일
        private String expectedDate; // 파산 예정일
    }
}

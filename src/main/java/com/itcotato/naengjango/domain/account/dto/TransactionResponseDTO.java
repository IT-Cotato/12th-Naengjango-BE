package com.itcotato.naengjango.domain.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TransactionResponseDTO {
    /**
     * 문자 내역 파싱 응답 DTO
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "문자 파싱 결과 응답 정보")
    public static class ParseResponseDTO {
        @Schema(description = "분류 (수입/지출)", example = "지출")
        private String type;        // 분류(수입/지출)

        @Schema(description = "금액", example = "15800")
        private Long amount;        // 금액

        @Schema(description = "업체명 또는 내역", example = "스타벅스")
        private String description; // 내역

        @Schema(description = "문자 원문", example = "[Web발신] 삼성카드 승인 15,800원 ...")
        private String memo;        // 원문

        @Schema(description = "추출된 날짜", example = "2026-01-19")
        private String date;        // 날짜

        @Schema(description = "카테고리", example = "식비")
        private String category;    // 카테고리
    }

    /**
     * 날짜별 가계부 내역 조회 관련 DTO
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "날짜별 가계부 내역 조회 응답 정보")
    public static class TransactionListDTO {
        @Schema(description = "분류 (수입/지출)", example = "지출")
        private String type;        // 분류(수입/지출)

        @Schema(description = "금액", example = "15800")
        private Long amount;        // 금액

        @Schema(description = "업체명 또는 내역", example = "스타벅스")
        private String description; // 내역

        @Schema(description = "메모", example = "아이스 아메리카노 외 1건")
        private String memo;        // 메모

        @Schema(description = "날짜", example = "2026-01-19")
        private String date;        // 날짜 (yyyy-MM-dd)

        @Schema(description = "카테고리", example = "식비")
        private String category;    // 카테고리
    }

    /**
     * 가계부 내역 저장 관련 DTO
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "가계부 내역 저장 응답 정보")
    public static class CreateResultDTO {
        private Long transactionId;     // transaction_id 반환
    }
}

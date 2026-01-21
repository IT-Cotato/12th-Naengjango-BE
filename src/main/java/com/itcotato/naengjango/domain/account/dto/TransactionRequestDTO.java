package com.itcotato.naengjango.domain.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TransactionRequestDTO {

    /**
     * 문자 파싱 요청 DTO
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "문자 파싱 요청 정보")
    public static class ParseRequestDTO {
        @Schema(description = "문자 원문", example = "[Web발신]\n삼성0000승인 김*수\n15,800원 일시불\n01/19 14:20 스타벅스")
        private String rawText; //문자 원문
    }

    /**
     * 가계부 내역 저장 요청 DTO
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "가계부 내역 생성 요청 정보")
    public static class CreateDTO {
        @Schema(description = "거래 타입 (수입/지출)", example = "지출")
        private String type;        // 수입/지출

        @Schema(description = "금액", example = "15800")
        private Long amount;        // 금액

        @Schema(description = "업체명 또는 내역", example = "스타벅스")
        private String description; // 업체명

        @Schema(description = "메모", example = "아이스 아메리카노 외 1건")
        private String memo;        // 메모(문자 원문)

        @Schema(description = "결제 날짜", example = "2026-01-19")
        private String date;        // 결제 날짜

        @Schema(description = "카테고리", example = "식비")
        private String category;    // 카테고리
    }

    /**
     * 가계부 내역 수정 관련 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "가계부 내역 수정 요청 정보")
    public static class UpdateDTO {
        @Schema(description = "분류(수입/지출)", example = "지출")
        private String type;        // 분류(수입/지출)

        @Schema(description = "금액", example = "20000")
        private Long amount;        // 금액

        @Schema(description = "내역", example = "교촌치킨")
        private String description; // 내역

        @Schema(description = "메모", example = "친구들과 저녁 식사")
        private String memo;        // 메모

        @Schema(description = "카테고리", example = "식비")
        private String category;    // 카테고리
    }
}

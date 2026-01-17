package com.itcotato.naengjango.domain.account.dto;

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
    public static class ParseRequestDTO {
        private String rawText; //문자 원문
    }

    /**
     * 가계부 내역 저장 요청 DTO
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDTO {
        private String type;        // 수입/지출
        private Long amount;        // 금액
        private String description; // 업체명
        private String memo;        // 메모(문자 원문)
        private String date;        // 결제 날짜
        private String category;    // 카테고리
    }

    /**
     * 가계부 내역 수정 관련 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateDTO {
        private String type;        // 분류(수입/지출)
        private Long amount;        // 금액
        private String description; // 내역
        private String memo;        // 메모
        private String category;    // 카테고리
    }
}

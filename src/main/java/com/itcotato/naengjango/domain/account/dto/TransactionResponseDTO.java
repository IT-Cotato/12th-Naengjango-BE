package com.itcotato.naengjango.domain.account.dto;

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
    public static class ParseResponseDTO {
        private String type;        // 수입/지출
        private Long amount;        // 금액
        private String description; // 업체명
        private String memo;        // 원문
        private String date;        // 날짜
        private String category;    // 카테고리
    }
}

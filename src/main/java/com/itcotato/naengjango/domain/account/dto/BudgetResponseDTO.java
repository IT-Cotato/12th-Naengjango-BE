package com.itcotato.naengjango.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 남은 예산 정보(오늘 예산/이번달 예산) 조회 응답 DTO
 */

public class BudgetResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetStatusDTO {
        private Integer todayRemaining;   // 오늘 남은 예산
        private Integer monthRemaining;   // 이달의 남은 예산
    }
}

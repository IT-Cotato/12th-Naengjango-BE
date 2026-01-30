package com.itcotato.naengjango.domain.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "예산 현황 응답 정보")
    public static class BudgetStatusDTO {
        @Schema(description = "오늘 남은 예산", example = "5000")
        private Integer todayRemaining;   // 오늘 남은 예산

        @Schema(description = "이달의 남은 예산", example = "150000")
        private Integer monthRemaining;   // 이달의 남은 예산
    }
}

package com.itcotato.naengjango.domain.freeze.dto;

import com.itcotato.naengjango.domain.freeze.enums.FreezeStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public class FreezeResponseDto {

    /**
     * 냉동 생성 응답
     */
    public record Create(
            Long freezeId,
            LocalDateTime expiresAt
    ) {}

    /**
     * 냉동 중 항목 조회 응답
     */
    public record Item(
            Long id,
            String appName,
            String itemName,
            int price,
            LocalDateTime frozenAt,
            LocalDateTime expiresAt,
            long remainingSeconds
    ) {}

    /**
     * 다중 액션 결과 응답
     * - 성공 / 실패 / 연장
     */
    public record BulkAction(
            ActionResult action,
            StatusSnapshot status
    ) {}

    /**
     * 상태 스냅샷 응답
     */
    public record ActionResult(
            int affectedCount,
            int snowballsGranted
    ) {}

    /**
     * 요청 이후 "현재 사용자 상태
     */
    public record StatusSnapshot(
            int currentSnowballBalance,
            boolean isStreak,
            int streakDays
    ) {}

    /**
     * 예산 미리보기 응답
     */
    public record BudgetPreview(
            int selectedTotalPrice,
            int remainingDaysInMonth,
            int perDayBudget
    ) {}

    /**
     * 링크 파싱 성공 응답
     */
    public record LinkParse(
            String appName,
            String itemName,
            int price
    ) {}

    /**
     * 특정 냉동 품목 상세 조회 응답
     */
    public record Detail(
            Long id,
            String appName,
            String itemName,
            int price,
            LocalDateTime frozenAt,
            LocalDateTime expiresAt,
            LocalDateTime updatedAt
    ) {}
}

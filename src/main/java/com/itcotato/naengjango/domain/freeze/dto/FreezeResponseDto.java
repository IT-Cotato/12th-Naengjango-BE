package com.itcotato.naengjango.domain.freeze.dto;

import com.itcotato.naengjango.domain.freeze.enums.FreezeStatus;

import java.time.LocalDateTime;

public class FreezeResponseDto {

    /**
     * 냉동 등록 응답
     */
    public record CreateResponse(
            Long freezeItemId,
            String itemName,
            int price,
            FreezeStatus status,
            LocalDateTime frozenAt,
            LocalDateTime deadline
    ) {}

    /**
     * 냉동 목록 응답
     */
    public record ListResponse(
            Long freezeItemId,
            String itemName,
            int price,
            FreezeStatus status,
            LocalDateTime deadline
    ) {}

    /**
     * 냉동 상세 응답
     */
    public record DetailResponse(
            Long freezeItemId,
            String appName,
            String itemName,
            int price,
            FreezeStatus status,
            LocalDateTime frozenAt,
            LocalDateTime deadline
    ) {}
}

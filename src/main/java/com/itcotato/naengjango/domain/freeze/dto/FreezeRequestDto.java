package com.itcotato.naengjango.domain.freeze.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

public class FreezeRequestDto {

    /**
     * 냉동 생성 (수동 등록)
     */
    public record Create(
            String appName,
            String itemName,
            Long price
    ) {}

    /**
     * 냉동 수정
     */
    public record Update(
            String appName,
            String itemName,
            Long price
    ) {}

    /**
     * 다중 선택용 ID 리스트
     * - 성공 / 실패 / 연장 / 예산 계산
     */
    public record Ids(
            List<Long> freezeIds
    ) {}
}

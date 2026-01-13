package com.itcotato.naengjango.domain.freeze.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

public class FreezeRequestDto {

    /**
     * 냉동 등록 요청
     */
    public record CreateRequest(
            @Schema(
                    description = "구매 예정 앱 이름",
                    example = "쿠팡"
            )
            @NotBlank String appName,

            @Schema(
                    description = "상품 이름",
                    example = "에어팟 프로 2세대"
            )
            @NotBlank String itemName,

            @Schema(
                    description = "상품 가격 (원)",
                    example = "329000"
            )
            @Positive int price
    ) {}

    /**
     * 냉동 일괄 처리 요청
     */
    public record BatchRequest(

            @Schema(
                    description = "처리할 냉동 항목 ID 목록",
                    example = "[1, 2, 3]"
            )
            List<Long> freezeItemIds
    ) {}

    /**
     * 냉동 수정 요청
     */
    public record UpdateRequest(

            @Schema(
                    description = "구매 예정 앱 이름",
                    example = "무신사"
            )
            String appName,

            @Schema(
                    description = "상품 이름",
                    example = "후드티"
            )
            String itemName,

            @Schema(
                    description = "상품 가격 (원)",
                    example = "189000"
            )
            Integer price
    ) {}
}

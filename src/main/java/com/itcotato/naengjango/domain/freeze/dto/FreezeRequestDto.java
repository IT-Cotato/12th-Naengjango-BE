package com.itcotato.naengjango.domain.freeze.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class FreezeRequestDto {

    /**
     * 냉동 등록 요청
     */
    public record CreateRequest(
            @NotBlank String appName,
            @NotBlank String itemName,
            @Positive int price
    ) {}
}

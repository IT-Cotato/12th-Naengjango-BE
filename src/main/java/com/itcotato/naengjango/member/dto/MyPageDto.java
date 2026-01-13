package com.itcotato.naengjango.member.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class MyPageDto {

    public record MeResponse(
            Long userId,
            String name,
            String loginId,
            String phoneNumber,
            Integer budget,
            String socialType,
            String role
    ) {}

    public record UpdateBudgetRequest(
            @NotNull
            @Min(0)
            @Max(2_000_000_000)
            Integer budget
    ) {}

    public record BudgetResponse(
            Integer budget
    ) {}
}

package com.itcotato.naengjango.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthRequestDto {

    public record LoginRequest (
            @NotBlank String loginId,
            @NotBlank String password
    ) {}
}

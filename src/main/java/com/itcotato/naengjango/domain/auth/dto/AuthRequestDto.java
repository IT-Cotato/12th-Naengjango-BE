package com.itcotato.naengjango.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthRequestDto {

    public record LoginRequest (
            @NotBlank String loginId,
            @NotBlank String password
    ) {}

    public record FindLoginIdRequest(
            String name,
            String phoneNumber
    ) {}

    public record FindPasswordRequest(
            String name,
            String phoneNumber,
            String loginId
    ) {}

    public record RefreshTokenRequest(
            @NotBlank String refreshToken
    ) {}
}

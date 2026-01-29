package com.itcotato.naengjango.domain.auth.dto;

public class AuthResponseDto {

    public record TokenResponse (
            String accessToken,
            String refreshToken,
            boolean signupCompleted
    ) {}

    public record OAuth2LoginResult (
            String accessToken,
            String refreshToken,
            boolean signupCompleted,
            String socialType,
            String socialId,
            String name
    ) {}
}

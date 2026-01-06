package com.itcotato.naengjango.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로그인 응답 DTO
 */
@Getter
@AllArgsConstructor
@Schema(description = "로그인 응답 DTO")
public class LoginResponseDto {

    @Schema(
            description = "액세스 토큰 (JWT)",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String accessToken;

    @Schema(
            description = "리프레시 토큰 (JWT)",
            example = "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4uLi4="
    )
    private String refreshToken;
}

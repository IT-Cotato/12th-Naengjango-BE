package com.itcotato.naengjango.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 소셜 로그인 요청 DTO
 */
@Getter
@NoArgsConstructor
@Schema(description = "소셜 로그인 요청 DTO")
public class SocialLoginRequestDto {

    @Schema(
            description = "소셜 토큰",
            example = "eyJhbGciOiJSUzI1NiIsImtpZCI6Ij...")
    @NotBlank
    private String socialToken;
}

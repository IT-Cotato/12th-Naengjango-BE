package com.itcotato.naengjango.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그아웃 요청 DTO
 */
@Getter
@NoArgsConstructor
@Schema(description = "로그아웃 요청 DTO")
public class LogoutRequestDto {

    @Schema(
            description = "리프레시 토큰",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    @NotBlank
    private String refreshToken;
}

package com.itcotato.naengjango.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 토큰 재발급 응답 DTO
 */
@Getter
@AllArgsConstructor
@Schema(description = "토큰 재발급 응답 DTO")
public class TokenReissueResponseDto {

    @Schema(
            description = "재발급된 액세스 토큰",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String accessToken;
}

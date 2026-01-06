package com.itcotato.naengjango.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로컬 로그인 요청 DTO
 */
@Getter
@NoArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class LocalLoginRequestDto {

    @Schema(
            description = "사용자 아이디",
            example = "user123"
    )
    @NotBlank
    private String loginId;

    @Schema(
            description = "사용자 비밀번호",
            example = "password123!"
    )
    @NotBlank
    private String password;
}

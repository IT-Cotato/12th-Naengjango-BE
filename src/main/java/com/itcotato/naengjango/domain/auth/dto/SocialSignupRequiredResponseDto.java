package com.itcotato.naengjango.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 소셜 회원가입 필요 응답 DTO
 * 구글 아이디로 로그인 눌렀을 때, 추가 정보를 받아 회원가입 진행해야 하는 경우 반환
 */
@Getter
@AllArgsConstructor
@Schema(description = "소셜 회원가입 필요 응답 DTO")
public class SocialSignupRequiredResponseDto {

    @Schema(
            description = "회원가입 필요 여부",
            example = "true"
    )
    private boolean needSignup; //항사 true

    @Schema(
            description = "소셜 타입",
            example = "GOOGLE, LOCAL"
    )
    private String socialType;

    @Schema(
            description = "소셜 제공자 내부 사용자 ID",
            example = "1234567890"
    )
    private String socialId;
}

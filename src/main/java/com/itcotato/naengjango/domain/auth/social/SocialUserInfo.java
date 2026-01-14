package com.itcotato.naengjango.domain.auth.social;

import com.itcotato.naengjango.domain.member.enums.SocialType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 소셜 로그인 인증 결과 DTO
 * - 소셜 제공자
 * - 소셜 제공자 내부 사용자 ID
 */
@Getter
@RequiredArgsConstructor
public class SocialUserInfo {

    private final SocialType socialType;
    private final String socialId;
}

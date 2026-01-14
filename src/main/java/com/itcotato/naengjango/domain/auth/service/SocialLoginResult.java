package com.itcotato.naengjango.domain.auth.service;

import com.itcotato.naengjango.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 소셜 로그인 결과 DTO
 * - linked: 소셜 계정이 기존 회원과 연동되었는지 여부
 * - member: linked=true일 때 기존 회원 정보
 * - socialId: linked=false일 때 소셜 제공자 내부 사용자 ID
 */
@Getter
@AllArgsConstructor
public class SocialLoginResult {

    private final boolean linked;
    private final Member member;    // linked=true일 때만
    private final String socialId;  // linked=false일 때만

    // 팩토리 메서드
    public static SocialLoginResult linked(Member member) {
        return new SocialLoginResult(true, member, null);
    }

    // 팩토리 메서드
    public static SocialLoginResult notLinked(String socialId) {
        return new SocialLoginResult(false, null, socialId);
    }

    // linked 여부 확인
    public boolean isLinked() {
        return linked;
    }
}

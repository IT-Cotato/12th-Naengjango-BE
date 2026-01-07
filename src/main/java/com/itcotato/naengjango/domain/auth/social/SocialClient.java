package com.itcotato.naengjango.domain.auth.social;

/**
 * 소셜 로그인 클라이언트 인터페이스
 * access token을 이용해 소셜 사용자 정보를 조회한다.
 */
public interface SocialClient {

    /**
     * 소셜 access token 검증 및 사용자 정보 조회
     *
     * @param accessToken 소셜 access token
     * @return SocialUserInfo (socialType, socialId)
     */
    SocialUserInfo getUserInfo(String accessToken);
}

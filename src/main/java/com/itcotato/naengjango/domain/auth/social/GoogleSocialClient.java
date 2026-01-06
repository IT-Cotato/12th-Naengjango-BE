package com.itcotato.naengjango.domain.auth.social;

import com.itcotato.naengjango.domain.auth.exception.AuthException;
import com.itcotato.naengjango.domain.auth.exception.code.AuthErrorCode;
import com.itcotato.naengjango.domain.member.enums.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Google 소셜 로그인 클라이언트
 */
@Component
@RequiredArgsConstructor
public class GoogleSocialClient implements SocialClient {

    private static final String GOOGLE_USERINFO_URL =
            "https://www.googleapis.com/oauth2/v3/userinfo";

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 액세스 토큰으로 Google 사용자 정보 조회
     */
    @Override
    public SocialUserInfo getUserInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    GOOGLE_USERINFO_URL,
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            String socialId = (String) response.getBody().get("sub");

            if (socialId == null) {
                throw new AuthException(AuthErrorCode.LOGIN_INVALID_SOCIAL_TOKEN);
            }

            return new SocialUserInfo(SocialType.GOOGLE, socialId);

        } catch (Exception e) {
            throw new AuthException(AuthErrorCode.LOGIN_INVALID_SOCIAL_TOKEN);
        }
    }
}

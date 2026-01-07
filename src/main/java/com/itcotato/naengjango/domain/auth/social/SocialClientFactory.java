package com.itcotato.naengjango.domain.auth.social;

import com.itcotato.naengjango.domain.auth.exception.AuthException;
import com.itcotato.naengjango.domain.auth.exception.code.AuthErrorCode;
import com.itcotato.naengjango.domain.member.enums.SocialType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 소셜 클라이언트 팩토리
 * - 소셜 타입에 맞는 소셜 클라이언트를 반환
 */
@Component
public class SocialClientFactory {

    private final Map<SocialType, SocialClient> clientMap;

    /**
     * 생성자 주입을 통해 사용 가능한 소셜 클라이언트들을 맵에 등록
     */
    public SocialClientFactory(List<SocialClient> clients) {

        this.clientMap = new EnumMap<>(SocialType.class);
        clients.forEach(client -> {
            if (client instanceof GoogleSocialClient googleClient) {
                clientMap.put(SocialType.GOOGLE, googleClient);
            }
        });
    }

    public SocialClient getClient(SocialType socialType) {
        SocialClient client = clientMap.get(socialType);
        if (client == null) {
            throw new AuthException(AuthErrorCode.LOGIN_UNSUPPORTED_SOCIAL_TYPE);
        }
        return client;
    }
}

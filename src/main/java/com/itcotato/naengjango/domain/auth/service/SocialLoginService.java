package com.itcotato.naengjango.domain.auth.service;

import com.itcotato.naengjango.domain.auth.social.SocialClient;
import com.itcotato.naengjango.domain.auth.social.SocialClientFactory;
import com.itcotato.naengjango.domain.auth.social.SocialUserInfo;
import com.itcotato.naengjango.domain.member.enums.SocialType;
import com.itcotato.naengjango.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 소셜 로그인 서비스
 */
@Service
@RequiredArgsConstructor
public class SocialLoginService {

    private final SocialClientFactory socialClientFactory;
    private final MemberRepository memberRepository;

    /**
     * 소셜 로그인
     * - 기존 연동 계정: Member 반환
     * - 최초 연동 계정: socialId 반환
     */
    @Transactional
    public SocialLoginResult login(
            SocialType socialType,
            String socialToken
    ) {
        // 1. 소셜 토큰 검증 → socialId 추출
        SocialClient client = socialClientFactory.getClient(socialType);
        SocialUserInfo userInfo = client.getUserInfo(socialToken);

        // 2. 기존 연동 계정 조회
        return memberRepository
                .findBySocialTypeAndSocialId(
                        userInfo.getSocialType(),
                        userInfo.getSocialId()
                )
                .map(SocialLoginResult::linked)
                .orElseGet(() ->
                        SocialLoginResult.notLinked(userInfo.getSocialId())
                );
    }
}

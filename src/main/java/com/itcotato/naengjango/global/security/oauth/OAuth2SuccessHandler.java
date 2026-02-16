package com.itcotato.naengjango.global.security.oauth;

import com.itcotato.naengjango.domain.auth.dto.AuthResponseDto;
import com.itcotato.naengjango.domain.auth.service.AuthService;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.member.enums.Role;
import com.itcotato.naengjango.domain.member.enums.SocialType;
import com.itcotato.naengjango.domain.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final AuthService authService;

    @Value("${app.front-url:https://12th-naengjango-fe.vercel.app}")
    private String frontUrl;


    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String googleId = oAuth2User.getAttribute("sub");
        String name = oAuth2User.getAttribute("name");

        // 1. 조회
        Member member = memberRepository.findBySocialId(googleId)
                .orElse(null);

        // 2️. 없으면 생성 (phoneNumber는 null 상태)
        if (member == null) {
            member = memberRepository.save(
                    Member.builder()
                            .name(name)
                            .socialType(SocialType.GOOGLE)
                            .socialId(googleId)
                            .role(Role.USER)
                            .phoneNumber(null)
                            .build()
            );
        }

        // 3. 토큰 발급
        AuthResponseDto.TokenResponse tokenResponse =
                authService.issueToken(member);

        boolean signupCompleted = member.getPhoneNumber() != null;

        // 4. 프론트로 redirect
        String targetUrl = UriComponentsBuilder
                .fromUriString(frontUrl + "/login")
                .queryParam("accessToken", tokenResponse.accessToken())
                .queryParam("refreshToken", tokenResponse.refreshToken())
                .queryParam("signupCompleted", signupCompleted)
                .build()
                .toUriString();

        response.sendRedirect(targetUrl);
    }
}


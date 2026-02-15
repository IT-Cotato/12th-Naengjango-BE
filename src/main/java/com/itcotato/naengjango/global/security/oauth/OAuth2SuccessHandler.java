package com.itcotato.naengjango.global.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itcotato.naengjango.domain.auth.dto.AuthResponseDto;
import com.itcotato.naengjango.domain.auth.exception.code.AuthSuccessCode;
import com.itcotato.naengjango.domain.auth.service.AuthService;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.member.enums.Role;
import com.itcotato.naengjango.domain.member.enums.SocialType;
import com.itcotato.naengjango.domain.member.repository.MemberRepository;
import com.itcotato.naengjango.global.apiPayload.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String googleId = oAuth2User.getAttribute("sub");
        String name = oAuth2User.getAttribute("name");

        Member member = memberRepository.findBySocialId(googleId)
                .orElseGet(() ->
                        memberRepository.save(
                                Member.builder()
                                        .name(name)
                                        .socialType(SocialType.GOOGLE)
                                        .socialId(googleId)
                                        .role(Role.USER)
                                        .build()
                        )
                );

        // 토큰 발급 공통화
        AuthResponseDto.TokenResponse tokenResponse =
                authService.issueToken(member);

        String accessToken = tokenResponse.accessToken();
        String refreshToken = tokenResponse.refreshToken();

        // 프론트 리다이렉트 주소
        String redirectUrl = "https://12th-naengjango-fe.vercel.app/home"
                + "?accessToken=" + accessToken
                + "&refreshToken=" + refreshToken;

        response.sendRedirect(redirectUrl);
    }
}

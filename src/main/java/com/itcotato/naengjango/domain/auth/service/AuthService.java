package com.itcotato.naengjango.domain.auth.service;

import com.itcotato.naengjango.domain.auth.dto.*;
import com.itcotato.naengjango.domain.auth.exception.AuthException;
import com.itcotato.naengjango.domain.auth.exception.code.AuthErrorCode;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.member.enums.SocialType;
import com.itcotato.naengjango.global.security.jwt.JwtProvider;
import com.itcotato.naengjango.global.redis.RefreshTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LocalLoginService localLoginService;
    private final SocialLoginService socialLoginService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    /**
     * 기본 로그인 (아이디 + 비밀번호)
     */
    @Transactional
    public LoginResponseDto loginLocal(LocalLoginRequestDto request) {

        // 1. 아이디/비밀번호 검증 → Member 반환
        Member member = localLoginService.login(
                request.getLoginId(),
                request.getPassword()
        );

        // 2. JWT 발급
        return issueTokens(member);
    }

    /**
     * 소셜 로그인
     * - 기존 연동 계정: JWT 발급
     * - 최초 연동 계정: 추가 정보 필요 응답
     */
    @Transactional
    public Object loginSocial(
            String socialType,
            SocialLoginRequestDto request
    ) {
        // 1. 소셜 타입 파싱
        SocialType type = parseSocialType(socialType);

        // 2. 소셜 로그인 시도
        SocialLoginResult result =
                socialLoginService.login(type, request.getSocialToken());

        // 3. 기존 연동 계정 → 로그인 완료
        if (result.isLinked()) {
            return issueTokens(result.getMember());
        }

        // 4. 최초 연동 계정 → 회원가입 필요
        return new SocialSignupRequiredResponseDto(
                true,
                type.name(),
                result.getSocialId()
        );
    }

    /**
     * Access Token 재발급
     */
    public TokenReissueResponseDto reissueAccessToken(
            TokenReissueRequestDto request
    ) {
        String refreshToken = request.getRefreshToken();

        // 1. refresh token 자체 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new AuthException(AuthErrorCode.REISSUE_INVALID_REFRESH_TOKEN);
        }

        // 2. Redis에서 memberId 조회
        Long memberId =
                refreshTokenRedisRepository.findMemberIdByToken(refreshToken);

        if (memberId == null) {
            throw new AuthException(AuthErrorCode.REISSUE_INVALID_REFRESH_TOKEN);
        }

        // 3. Access Token 재발급
        String newAccessToken =
                jwtProvider.createAccessToken(memberId);

        return new TokenReissueResponseDto(newAccessToken);
    }


    /**
     * Access / Refresh Token 발급 공통 메서드
     */
    private LoginResponseDto issueTokens(Member member) {

        String accessToken =
                jwtProvider.createAccessToken(member.getId());

        String refreshToken =
                jwtProvider.createRefreshToken(member.getId());

        refreshTokenRedisRepository.save(
                refreshToken,
                member.getId(),
                jwtProvider.getRefreshTokenExpirationMs()
        );

        return new LoginResponseDto(accessToken, refreshToken);
    }

    /**
     * 소셜 타입 문자열 파싱
     */
    private SocialType parseSocialType(String socialType) {
        try {
            return SocialType.valueOf(socialType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AuthException(AuthErrorCode.LOGIN_UNSUPPORTED_SOCIAL_TYPE);
        }
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(LogoutRequestDto request) {

        String refreshToken = request.getRefreshToken();

        // 1. JWT 형식 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new AuthException(AuthErrorCode.LOGOUT_INVALID_REFRESH_TOKEN);
        }

        // 2. Redis에 존재하는지 확인
        Long memberId =
                refreshTokenRedisRepository.findMemberIdByToken(refreshToken);

        if (memberId == null) {
            throw new AuthException(AuthErrorCode.LOGOUT_INVALID_REFRESH_TOKEN);
        }

        // 3. Refresh Token 삭제
        refreshTokenRedisRepository.delete(refreshToken);
    }
}

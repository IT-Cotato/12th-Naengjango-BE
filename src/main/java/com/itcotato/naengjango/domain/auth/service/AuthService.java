package com.itcotato.naengjango.domain.auth.service;

import com.itcotato.naengjango.domain.auth.dto.AuthRequestDto;
import com.itcotato.naengjango.domain.auth.dto.AuthResponseDto;
import com.itcotato.naengjango.domain.auth.exception.AuthException;
import com.itcotato.naengjango.domain.auth.exception.code.AuthErrorCode;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.member.enums.SocialType;
import com.itcotato.naengjango.domain.member.exception.MemberException;
import com.itcotato.naengjango.domain.member.exception.code.MemberErrorCode;
import com.itcotato.naengjango.domain.member.repository.MemberRepository;
import com.itcotato.naengjango.domain.member.service.SmsService;
import com.itcotato.naengjango.global.redis.RefreshTokenRedisRepository;
import com.itcotato.naengjango.global.security.jwt.JwtClaims;
import com.itcotato.naengjango.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final SmsService smsService;


    /** 토큰 발급 공통 메서드 */
    public AuthResponseDto.TokenResponse issueToken(Member member) {

        // 1. 가입 완료 여부 판단
        boolean signupCompleted =
                member.getPhoneNumber() != null
                        && !member.getMemberAgreements().isEmpty();

        // 2. Claims 생성
        JwtClaims claims = new JwtClaims(
                member.getId(),
                member.getRole().name(),
                signupCompleted
        );

        // 3. 토큰 발급
        String accessToken = jwtProvider.createAccessToken(claims);
        String refreshToken = jwtProvider.createRefreshToken(claims);

        // 4. RefreshToken Redis 저장
        try {
            refreshTokenRedisRepository.save(
                    member.getId(),
                    refreshToken,
                    Duration.ofSeconds(jwtProvider.getRefreshTokenExpireSeconds())
            );} catch (Exception e) {
            throw e;
        }
        // 5. 공통 응답 DTO
        return new AuthResponseDto.TokenResponse(
                accessToken,
                refreshToken,
                signupCompleted
        );
    }

    /** 기본 로그인 (아이디 + 비밀번호) */
    @Transactional
    public AuthResponseDto.TokenResponse localLogin(
            AuthRequestDto.LoginRequest request
    ) {
        Member member = memberRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new AuthException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (member.getSocialType() != SocialType.LOCAL) {
            throw new AuthException(AuthErrorCode.LOGIN_INVALID_TYPE);
        }

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new AuthException(AuthErrorCode.LOGIN_INVALID_PASSWORD);
        }

        return issueToken(member);
    }

    /** 토큰 재발급 (Access Token 재발급) */
    public AuthResponseDto.TokenResponse refresh(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(MemberErrorCode.MEMBER_NOT_FOUND));

        return issueToken(member);
    }

    /** 로그아웃 */
    @Transactional
    public void logout(Long memberId) {
        refreshTokenRedisRepository.delete(memberId);
    }

    public String findLoginId(String name, String phoneNumber) {
        Member member = memberRepository
                .findByNameAndPhoneNumber(name, phoneNumber)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return maskLoginId(member.getLoginId());
    }

    /**
     * 아이디 마스킹 (뒤 2~6자리 * 처리)
     */
    private String maskLoginId(String loginId) {
        int length = loginId.length();
        int maskStart = Math.max(1, length - 6);
        int maskEnd = length - 2;

        StringBuilder sb = new StringBuilder(loginId);
        for (int i = maskStart; i < maskEnd; i++) {
            sb.setCharAt(i, '*');
        }
        return sb.toString();
    }

    /**
     * 임시 비밀번호 생성
     */
    private String generateTempPassword() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8);
    }

    @Transactional
    public void resetPassword(String name, String loginId, String phoneNumber) {
        Member member = memberRepository
                .findByNameAndLoginIdAndPhoneNumber(name, loginId, phoneNumber)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 1. 임시 비밀번호 생성
        String tempPassword = generateTempPassword();

        // 2. 비밀번호 암호화 후 변경
        member.changePassword(passwordEncoder.encode(tempPassword));

        // 3. SMS 발송
        smsService.sendTempPassword(phoneNumber, tempPassword);
    }
}
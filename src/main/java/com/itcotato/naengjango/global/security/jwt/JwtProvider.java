package com.itcotato.naengjango.global.security.jwt;

import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증을 담당하는 클래스
 */
@Component
public class JwtProvider {

    private static final String CLAIM_MEMBER_ID = "memberId";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_SIGNUP_COMPLETED = "signupCompleted";

    private final JwtProperties properties;
    private final MemberRepository memberRepository;
    private final SecretKey secretKey;

    public JwtProvider(
            JwtProperties properties,
            MemberRepository memberRepository) {
        this.properties = properties;
        this.secretKey = Keys.hmacShaKeyFor(
                properties.secret().getBytes(StandardCharsets.UTF_8)
        );
        this.memberRepository = memberRepository;
    }

    /* =========================
       토큰 생성
       ========================= */

    public String createAccessToken(JwtClaims claims) {
        return createToken(claims, properties.accessExpSeconds());
    }

    public String createRefreshToken(JwtClaims claims) {
        return createToken(claims, properties.refreshExpSeconds());
    }

    private String createToken(JwtClaims claims, long expireSeconds) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(expireSeconds);

        return Jwts.builder()
                .setSubject(String.valueOf(claims.memberId()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claim(CLAIM_MEMBER_ID, claims.memberId())
                .claim(CLAIM_ROLE, claims.role())
                .claim(CLAIM_SIGNUP_COMPLETED, claims.signupCompleted())
                .signWith(secretKey)
                .compact();
    }

    /* =========================
       토큰 검증 + Claims 추출
       ========================= */

    public JwtClaims extractClaims(String token) {
        Claims claims = parse(token);

        Long memberId = claims.get(CLAIM_MEMBER_ID, Long.class);
        String role = claims.get(CLAIM_ROLE, String.class);
        Boolean signupCompleted = claims.get(CLAIM_SIGNUP_COMPLETED, Boolean.class);

        if (memberId == null || role == null || signupCompleted == null) {
            throw new JwtException("Invalid JWT claims");
        }

        return new JwtClaims(
                memberId,
                role,
                signupCompleted
        );
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getRefreshTokenExpireSeconds() {
        return properties.refreshExpSeconds();
    }

    public JwtClaims validateAndExtractClaims(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new JwtClaims(
                Long.valueOf(claims.getSubject()),
                claims.get("role", String.class),
                claims.get("signupCompleted", Boolean.class)
        );
    }

    /**
     * JWT → Member 조회
     * - 인증 필터에서 사용
     */
    public Member getMember(String token) {
        JwtClaims claims = extractClaims(token);

        return memberRepository.findById(claims.memberId())
                .orElseThrow(() ->
                        new IllegalStateException("Member not found. memberId=" + claims.memberId())
                );
    }
}

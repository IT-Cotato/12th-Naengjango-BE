package com.itcotato.naengjango.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

/**
 * 리프레시 토큰 Redis 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 리프레시 토큰 저장
     *
     * @param refreshToken     리프레시 토큰
     * @param memberId         회원 ID
     * @param expirationMillis 만료 시간 (밀리초)
     */
    public void save(
            String refreshToken,
            Long memberId,
            long expirationMillis
    ) {
        redisTemplate.opsForValue().set(
                getKey(refreshToken),
                memberId.toString(),
                expirationMillis,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * 리프레시 토큰으로 회원 ID 조회
     *
     * @param refreshToken 리프레시 토큰
     * @return 회원 ID, 없으면 null
     */
    public Long findMemberIdByToken(String refreshToken) {
        String value = redisTemplate.opsForValue().get(getKey(refreshToken));
        return value == null ? null : Long.valueOf(value);
    }

    /**
     * 리프레시 토큰 삭제
     *
     * @param refreshToken 리프레시 토큰
     */
    public void delete(String refreshToken) {
        redisTemplate.delete(getKey(refreshToken));
    }

    /** 리프레시 토큰 키 조회 */
    private String getKey(String refreshToken) {
        return "refresh:" + refreshToken;
    }
}

package com.itcotato.naengjango.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

/**
 * 리프레시 토큰 Redis 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository {

    private static final String KEY_PREFIX = "Refresh:";

    private final StringRedisTemplate redisTemplate;

    /** 리프레시 토큰 저장 */
    public void save(Long memberId, String refreshToken, Duration ttl) {
        redisTemplate.opsForValue()
                .set(key(memberId), refreshToken, ttl);
    }

    /**
     * 리프레시 토큰 조회
     */
    public Optional<String> findByMemberId(Long memberId) {
        return Optional.ofNullable(
                redisTemplate.opsForValue()
                        .get(KEY_PREFIX + memberId)
        );
    }

    /** 리프레시 토큰 삭제 (로그아웃) */
    public void delete(Long memberId) {
        redisTemplate.delete(key(memberId));
    }

    private String key(Long memberId) {
        return KEY_PREFIX + memberId;
    }

}

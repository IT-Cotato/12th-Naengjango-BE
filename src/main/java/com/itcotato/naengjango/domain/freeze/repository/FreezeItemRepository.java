package com.itcotato.naengjango.domain.freeze.repository;

import com.itcotato.naengjango.domain.freeze.entity.FreezeItem;
import com.itcotato.naengjango.domain.freeze.enums.FreezeStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface FreezeItemRepository extends JpaRepository<FreezeItem, Integer> {
    /**
     * 분석 리포트 관련
     */

    // 특정 기간 동안 냉동 성공(SUCCESS)한 냉동 상품의 가격 총합을 조회합니다.
    @Query("SELECT SUM(f.price) FROM FreezeItem f " +
            "WHERE f.member.id = :memberId " +
            "AND f.status = :status " +
            "AND f.frozenAt >= :start " +
            "AND f.frozenAt <= :end")
    Long sumPriceByMemberAndStatus(@Param("memberId") Long memberId,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end,
                                   @Param("status") FreezeStatus status);

    // 특정 기간 동안 전체 냉동 상품 개수를 조회합니다.
    long countByMemberIdAndFrozenAtBetween(Long memberId, LocalDateTime start, LocalDateTime end);

    // 특정 기간 동안 냉동 성공한 냉동 상품 개수를 조회합니다.
    long countByMemberIdAndStatusAndFrozenAtBetween(Long memberId, FreezeStatus status, LocalDateTime start, LocalDateTime end);

    // 히트맵 계산을 위한 전체 냉동 데이터 리스트를 조회합니다.
    List<FreezeItem> findAllByMemberIdAndFrozenAtBetween(Long memberId, LocalDateTime start, LocalDateTime end);
}

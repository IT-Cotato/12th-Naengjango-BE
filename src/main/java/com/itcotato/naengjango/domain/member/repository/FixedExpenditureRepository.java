package com.itcotato.naengjango.domain.member.repository;

import com.itcotato.naengjango.domain.member.entity.FixedExpenditure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface FixedExpenditureRepository extends JpaRepository<FixedExpenditure, Long> {
    // 고정 지출 총합을 구하는 쿼리
    @Query("SELECT SUM(f.amount) FROM FixedExpenditure f WHERE f.member.id = :memberId")
    Long sumAmountByMember(@Param("memberId") Long memberId);
}

package com.itcotato.naengjango.domain.report.repository;

import com.itcotato.naengjango.domain.account.entity.Transaction;
import com.itcotato.naengjango.domain.report.dto.ReportResponseDTO;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Transaction, Long> {

    /**
     * 파산 시나리오 분석을 위해 사용자의 일별 지출 합계를 계산합니다.
     */

    @Query("SELECT function('DATE_FORMAT', t.date, '%Y-%m-%d'), SUM(t.amount) " +
            "FROM Transaction t " +
            "WHERE t.member.id = :memberId " +
            "AND t.type = 'EXPENSE' " +
            "AND t.date >= :start " +
            "GROUP BY function('DATE_FORMAT', t.date, '%Y-%m-%d') " +
            "ORDER BY function('DATE_FORMAT', t.date, '%Y-%m-%d') ASC")
    List<Object[]> findDailyTrendsRaw(@Param("memberId") Long memberId,
                                      @Param("start") LocalDateTime start);
}

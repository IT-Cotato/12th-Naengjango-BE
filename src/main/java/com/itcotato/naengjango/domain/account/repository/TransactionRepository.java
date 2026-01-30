package com.itcotato.naengjango.domain.account.repository;

import com.itcotato.naengjango.domain.account.entity.Transaction;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    /**
     * 예산 상태 조회 관련
     */
    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.member.id = :memberId " +
            "AND t.type = 'EXPENSE' " +
            "AND t.date BETWEEN :startDay AND :endDay")
    Long sumExpenseByMemberAndDate(@Param("memberId") Long memberId,
                                   @Param("startDay") LocalDateTime startDay,
                                   @Param("endDay") LocalDateTime endDay);


    /**
     * 날짜별 가계부 내역 조회 관련
     */
    List<Transaction> findAllByMemberIdAndDateBetween(
            Long memberId,
            LocalDateTime start,
            LocalDateTime end
    );
}

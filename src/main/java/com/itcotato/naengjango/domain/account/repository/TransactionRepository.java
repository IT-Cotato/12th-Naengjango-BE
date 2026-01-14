package com.itcotato.naengjango.domain.account.repository;

import com.itcotato.naengjango.domain.account.entity.Transaction;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.member.id = :memberId " +
            "AND t.type = 'EXPENSE' " +
            "AND t.date BETWEEN :startDay AND :endDay")
    Long sumExpenseByMemberAndDate(@Param("memberId") Long memberId,
                                   @Param("startDay") LocalDateTime startDay,
                                   @Param("endDay") LocalDateTime endDay);
}

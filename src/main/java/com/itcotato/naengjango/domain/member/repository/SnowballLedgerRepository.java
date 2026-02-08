package com.itcotato.naengjango.domain.member.repository;

import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.member.entity.SnowballLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface SnowballLedgerRepository extends JpaRepository<SnowballLedger, Long> {

    @Query("""
           select coalesce(sum(l.amount), 0)
           from SnowballLedger l
           where l.member = :member
           """)
    int sumBalance(@Param("member") Member member);

    @Query("""
           select coalesce(sum(l.amount), 0)
           from SnowballLedger l
           where l.member = :member
             and l.reason = :reason
             and l.createdAt >= :start
             and l.createdAt < :end
           """)
    int sumByReasonInRange(
            @Param("member") Member member,
            @Param("reason") String reason,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}

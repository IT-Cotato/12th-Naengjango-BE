package com.itcotato.naengjango.domain.snowball.repository;

import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.snowball.entity.SnowballLedger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<SnowballLedger> findByMemberOrderByCreatedAtDesc(
            Member member,
            Pageable pageable
    );

    @Query("""
    select coalesce(sum(l.amount), 0)
    from SnowballLedger l
    where l.member = :member
      and l.amount > 0
      and l.createdAt >= :start
      and l.createdAt < :end
""")
    int sumTodayEarned(
            @Param("member") Member member,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}

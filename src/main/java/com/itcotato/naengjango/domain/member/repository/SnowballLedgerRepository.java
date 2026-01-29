package com.itcotato.naengjango.domain.member.repository;

import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.member.entity.SnowballLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SnowballLedgerRepository extends JpaRepository<SnowballLedger, Long> {

    @Query("""
        select coalesce(sum(l.amount), 0)
        from SnowballLedger l
        where l.member = :member
    """)
    int sumByMember(Member member);
}

package com.itcotato.naengjango.domain.member.repository;

import com.itcotato.naengjango.domain.member.entity.Agreement;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.member.entity.MemberAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberAgreementRepository extends JpaRepository<MemberAgreement, Long> {

    Optional<MemberAgreement> findByMemberAndAgreement(
            Member member,
            Agreement agreement
    );
}

package com.itcotato.naengjango.domain.member.repository;

import com.itcotato.naengjango.domain.member.entity.MemberAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAgreementRepository extends JpaRepository<MemberAgreement, Long> {
}

package com.itcotato.naengjango.domain.user.repository;

import com.itcotato.naengjango.domain.user.entity.UserAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAgreementRepository extends JpaRepository<UserAgreement, Long> {
}

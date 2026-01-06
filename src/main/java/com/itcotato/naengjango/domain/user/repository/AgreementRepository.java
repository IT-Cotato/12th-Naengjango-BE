package com.itcotato.naengjango.domain.user.repository;

import com.itcotato.naengjango.domain.user.entity.Agreement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgreementRepository extends JpaRepository<Agreement, Long> {
}

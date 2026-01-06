package com.itcotato.naengjango.domain.member.repository;

import com.itcotato.naengjango.domain.member.entity.FixedExpenditure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FixedExpenditureRepository extends JpaRepository<FixedExpenditure, Long> {
}

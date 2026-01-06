package com.itcotato.naengjango.domain.user.repository;

import com.itcotato.naengjango.domain.user.entity.FixedExpenditure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FixedExpenditureRepository extends JpaRepository<FixedExpenditure, Long> {
}

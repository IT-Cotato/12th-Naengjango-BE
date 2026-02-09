package com.itcotato.naengjango.domain.member.repository;

import com.itcotato.naengjango.domain.member.entity.FixedExpenditure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FixedExpenditureRepository extends JpaRepository<FixedExpenditure, Long> {
    void deleteByMemberId(Long memberId);
    List<FixedExpenditure> findByMemberId(Long memberId);

}

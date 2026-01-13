package com.itcotato.naengjango.domain.freeze.repository;

import com.itcotato.naengjango.domain.freeze.entity.FreezeItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreezeItemRepository extends JpaRepository<FreezeItem, Integer> {
}

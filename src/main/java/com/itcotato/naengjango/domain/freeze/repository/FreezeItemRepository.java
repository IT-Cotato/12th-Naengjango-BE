package com.itcotato.naengjango.domain.freeze.repository;

import com.itcotato.naengjango.domain.freeze.entity.FreezeItem;
import com.itcotato.naengjango.domain.freeze.enums.FreezeStatus;
import com.itcotato.naengjango.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FreezeItemRepository extends JpaRepository<FreezeItem, Long> {

    List<FreezeItem> findAllByMemberOrderByFrozenAtDesc(Member member);

    List<FreezeItem> findAllByMemberAndStatusOrderByFrozenAtDesc(Member member, FreezeStatus status);

    List<FreezeItem> findAllByStatusAndDeadlineBefore(FreezeStatus status, LocalDateTime time);
}

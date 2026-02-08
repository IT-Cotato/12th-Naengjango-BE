package com.itcotato.naengjango.domain.favoriteapp.repository;

import com.itcotato.naengjango.domain.favoriteapp.entity.FavoriteApp;
import com.itcotato.naengjango.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteAppRepository extends JpaRepository<FavoriteApp, Long> {

    boolean existsByMemberAndAppName(Member member, String appName);

    List<FavoriteApp> findByMember(Member member);

    void deleteByIdAndMember(Long id, Member member);
}

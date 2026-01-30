package com.itcotato.naengjango.domain.member.repository;

import com.itcotato.naengjango.domain.member.entity.FavoriteApp;
import com.itcotato.naengjango.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteAppRepository extends JpaRepository<FavoriteApp, Long> {

    // 특정 회원의 모든 즐겨찾기 앱 조회
    List<FavoriteApp> findByMember(Member member);

    // 특정 회원이 특정 앱을 즐겨찾기에 추가했는지 확인
    boolean existsByMemberAndAppName(Member member, String appName);
}

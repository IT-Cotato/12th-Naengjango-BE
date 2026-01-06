package com.itcotato.naengjango.domain.member.repository;

import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.member.enums.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginIdAndSocialType(
            String loginId,
            SocialType socialType
    );

    Optional<Member> findBySocialTypeAndSocialId(
            SocialType socialType,
            String socialId
    );

    Optional<Member> findByLoginId(String loginId);
}

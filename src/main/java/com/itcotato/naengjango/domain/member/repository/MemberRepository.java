package com.itcotato.naengjango.domain.member.repository;

import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.member.enums.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // SMS 인증 시 휴대폰 번호로 가입된 유저가 있는지 확인
    boolean existsByPhoneNumber(String phoneNumber);
    // 회원가입 시 아이디 중복 체크
    boolean existsByLoginId(String loginId);
    // 소셜 타입과 소셜 ID로 회원가입 여부 확인
    boolean existsBySocialTypeAndSocialId(SocialType socialType, String socialId);

    Optional<Member> findByLoginIdAndSocialType(
            String loginId,
            SocialType socialType
    );

    Optional<Member> findBySocialTypeAndSocialId(
            SocialType socialType,
            String socialId
    );

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findBySocialId(String socialId);

    Optional<Member> findByNameAndPhoneNumber(String name, String phoneNumber);

    Optional<Member> findByNameAndLoginIdAndPhoneNumber(String name, String username, String phoneNumber);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);
}

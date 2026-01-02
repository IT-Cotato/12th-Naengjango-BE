package com.itcotato.naengjango.domain.user.repository;

import com.itcotato.naengjango.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // SMS 인증 시 휴대폰 번호로 가입된 유저가 있는지 확인
    boolean existsByPhoneNumber(String phoneNumber);
    // 회원가입 시 아이디 중복 체크
    boolean existsByLoginId(String loginId);
}
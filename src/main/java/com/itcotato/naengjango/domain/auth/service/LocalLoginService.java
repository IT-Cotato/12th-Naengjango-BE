package com.itcotato.naengjango.domain.auth.service;

import com.itcotato.naengjango.domain.auth.exception.AuthException;
import com.itcotato.naengjango.domain.auth.exception.code.AuthErrorCode;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.member.enums.SocialType;
import com.itcotato.naengjango.domain.member.exception.MemberException;
import com.itcotato.naengjango.domain.member.exception.code.MemberErrorCode;
import com.itcotato.naengjango.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 로컬(기본) 로그인 서비스
 */
@Service
@RequiredArgsConstructor
public class LocalLoginService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 기본 로그인 (아이디 + 비밀번호)
     */
    public Member login(String loginId, String password) {

        // 1. 아이디 존재 여부
        Member member = memberRepository
                .findByLoginIdAndSocialType(loginId, SocialType.LOCAL)
                .orElseThrow(() ->
                        new MemberException(MemberErrorCode.MEMBER_NOT_FOUND)
                );

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new AuthException(AuthErrorCode.LOGIN_INVALID_PASSWORD);
        }

        return member;
    }
}

package com.itcotato.naengjango.global.security;

import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.member.repository.MemberRepository;
import com.itcotato.naengjango.global.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentMemberProviderImpl implements CurrentMemberProvider{

    private final MemberRepository memberRepository;

    @Override
    public Member getCurrentMember() {

        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new IllegalStateException("로그인된 사용자 정보가 없습니다.");
        }

        Long memberId = userDetails.getMemberId();

        return memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new IllegalStateException("로그인 사용자 정보를 찾을 수 없습니다.")
                );
    }
}

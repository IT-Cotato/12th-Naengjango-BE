package com.itcotato.naengjango.domain.notification.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentMemberService {

    public Long getCurrentMemberId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("Unauthenticated");
        }

        // TODO: 프로젝트의 Principal 타입에 맞게 수정
        // 예: CustomUserDetails라면 ((CustomUserDetails) auth.getPrincipal()).getMemberId()
        if (auth.getPrincipal() instanceof Long memberId) {
            return memberId;
        }

        throw new IllegalStateException("Unsupported principal type: " + auth.getPrincipal().getClass());
    }
}

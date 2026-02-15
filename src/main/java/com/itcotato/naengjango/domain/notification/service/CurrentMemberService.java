package com.itcotato.naengjango.domain.notification.service;

import com.itcotato.naengjango.domain.member.entity.Member;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentMemberService {

    public Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("Unauthenticated request");
        }

        Object principal = authentication.getPrincipal();

        // principal이 Member로 들어오는 경우
        if (principal instanceof Member member) {
            return member.getId();
        }

        if (principal instanceof Long memberId) {
            return memberId;
        }

        if (principal instanceof String str) {
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid principal type: " + principal);
            }
        }

        throw new IllegalArgumentException("Unsupported principal type: " + principal.getClass().getName());
    }
}

package com.itcotato.naengjango.global.security.userdetails;

import com.itcotato.naengjango.domain.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

/**
 * Spring Security UserDetails 구현체
 * - Member 엔티티를 래핑하여 인증 및 권한 부여에 필요한 정보를 제공
 */
public class CustomUserDetails implements UserDetails {

    private final Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    /**
     * 사용자의 권한 정보를 반환
     * - ROLE_ 접두사를 붙여 SimpleGrantedAuthority 객체로 반환
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + member.getRole().name())
        );
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getLoginId();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    public Long getMemberId() {
        return member.getId();
    }
}

package com.itcotato.naengjango.global.security.jwt;

public record JwtClaims (
    Long memberId,
    String role,
    boolean signupCompleted
) {}

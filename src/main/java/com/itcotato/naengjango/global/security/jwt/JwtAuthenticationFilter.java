package com.itcotato.naengjango.global.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 인증 필터
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    /**
     * 요청 헤더에서 JWT 토큰을 추출하고 유효성을 검사하여
     * 인증 정보를 SecurityContext에 설정
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        log.info("scheme={}, secure={}", request.getScheme(), request.isSecure());

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 토큰 없으면 그냥 통과 (인증 안 된 상태로 컨트롤러에서 막힘)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // 토큰 검증 + 클레임 추출
            JwtClaims claims = jwtProvider.extractClaims(token);

            // 회원 가입 미완료 상태 제한
            if (!claims.signupCompleted()
                    && !isAllowedIncompleteSignup(request.getRequestURI())) {

                response.sendError(
                        HttpStatus.FORBIDDEN.value(),
                        "Signup is not completed"
                );
                return;
            }

            // 인증 객체 생성
            var authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + claims.role())
            );

            var authentication =
                    new UsernamePasswordAuthenticationToken(
                            claims.memberId(),
                            null,
                            authorities
                    );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

        } catch (Exception e) {
            // 토큰 문제 → 인증 실패
            SecurityContextHolder.clearContext();
            response.sendError(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid or expired token"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * signupCompleted=false 상태에서도 허용할 API
     */
    private boolean isAllowedIncompleteSignup(String uri) {
        return uri.startsWith("/auth/social")
                || uri.startsWith("/auth/logout");
    }
}

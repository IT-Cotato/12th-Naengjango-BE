package com.itcotato.naengjango.global.config;

import com.itcotato.naengjango.global.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /** 비밀번호 인코더 빈 등록 */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** 보안 필터 체인 설정 */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // 기본 설정 비활성화
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 세션 사용 안함
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // CORS 설정
                .cors(cors -> cors.configure(http))

                // 요청별 접근 제어
                .authorizeHttpRequests(auth -> auth
                // 인증 없이 허용
                        .requestMatchers(
                                "/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/**",
                                "/api/auth/login",
                                "/api/auth/login/**",
                                "/auth/login", "/auth/login/**"
                        ).permitAll()
                        // 로그인 없이 허용 가능한 경로
                        .requestMatchers("/api/sms/**", "/swagger-ui/**", "/v3/api-docs/**",
                                "/swagger-resources/**", "/error", "/api/members/**").permitAll()

                // 그 외 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // JWT 인증 필터 적용
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}

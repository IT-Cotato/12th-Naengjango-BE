package com.itcotato.naengjango.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (Postman/Swagger 테스트 시 403 방지)
                .csrf(csrf -> csrf.disable())

                // CORS 설정
                .cors(cors -> cors.configure(http))

                // 경로별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 로그인 없이 허용 가능한 경로
                        .requestMatchers("/api/sms/**", "/swagger-ui/**", "/v3/api-docs/**",
                                "/swagger-resources/**").permitAll()

                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}

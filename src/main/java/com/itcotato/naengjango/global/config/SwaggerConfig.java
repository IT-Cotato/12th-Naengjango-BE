package com.itcotato.naengjango.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI swagger() {
        Info info = new Info().title("Naengjango API").description("냉잔고 백엔드 API 문서").version("v1.0.0");

        return new OpenAPI()
                .info(info);
    }
}
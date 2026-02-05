package com.itcotato.naengjango.global.config;

import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI swagger() {
        io.swagger.v3.oas.models.info.Info info =
                new io.swagger.v3.oas.models.info.Info()
                        .title("Naengjango API")
                        .description("냉잔고 백엔드 API 문서")
                        .version("v1.0.0");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("BearerAuth");

        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .servers(List.of(
                        new Server()
                                .url("https://15.134.213.116.nip.io")
                                .description("Production server")
                ));
    }
}

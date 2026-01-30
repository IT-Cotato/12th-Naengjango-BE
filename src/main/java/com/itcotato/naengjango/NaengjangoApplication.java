package com.itcotato.naengjango;

import com.itcotato.naengjango.global.security.jwt.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class NaengjangoApplication {

	public static void main(String[] args) {
		SpringApplication.run(NaengjangoApplication.class, args);
	}

}

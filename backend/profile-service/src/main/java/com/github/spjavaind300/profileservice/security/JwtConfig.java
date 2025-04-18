package com.github.spjavaind300.profileservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret-key}")
    private String jwtSecret;

    @Bean("jwtSecret")
    public String getJwtSecret() {
        return jwtSecret;
    }


}


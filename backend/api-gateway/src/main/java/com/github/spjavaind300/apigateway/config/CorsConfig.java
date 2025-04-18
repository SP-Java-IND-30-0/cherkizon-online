package com.github.spjavaind300.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsFilter() {
        return new CorsWebFilter(source -> {
            CorsConfiguration config = new CorsConfiguration();

            config.addAllowedOrigin("http://localhost:3000");

            config.addAllowedMethod("*");

            config.addAllowedHeader("*");

            config.setAllowCredentials(true);

            config.addExposedHeader(HttpHeaders.AUTHORIZATION);

            return config;
        });
    }


}

package com.github.spjavaind300.commentsservice.config;

import com.github.spjavaind300.commentsservice.security.SecurityUtils;
import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            String token = SecurityUtils.getCurrentToken();
            if (token != null && !template.request().url().contains("/internal/")) {
                template.header("Authorization", "Bearer " + token);
            }
        };
    }
}
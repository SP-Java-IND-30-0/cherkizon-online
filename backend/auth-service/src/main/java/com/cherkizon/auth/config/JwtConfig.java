package com.cherkizon.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String secret;          // Секретный ключ для подписи
    private long expirationMs;      // Время жизни токена (в миллисекундах)
    private String issuer;          // Издатель токена (приложение)

}
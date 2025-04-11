package com.github.spjavaind300;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableFeignClients
@EnableRetry
public class AdsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdsServiceApplication.class, args);
    }
}
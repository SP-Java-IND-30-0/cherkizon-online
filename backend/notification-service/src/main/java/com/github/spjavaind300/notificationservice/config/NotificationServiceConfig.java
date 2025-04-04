package com.github.spjavaind300.notificationservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationServiceConfig {

    @Value("${mail.template.caption}")
    private String caption;

    @Value("${mail.template.support-link}")
    String supportLink;

    @Bean("caption")
    public String getCaption() {
        return caption;
    }

    @Bean("supportLink")
    public String getSupportLink() {
        return supportLink;
    }
}

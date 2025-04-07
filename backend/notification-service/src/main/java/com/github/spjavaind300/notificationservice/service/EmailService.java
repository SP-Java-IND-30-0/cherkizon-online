package com.github.spjavaind300.notificationservice.service;

import com.github.spjavaind300.notificationservice.model.dto.EmailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface EmailService {

    Logger log = LoggerFactory.getLogger(EmailService.class);

    void sendEmailAsync(EmailDto emailDto);

    default void sendEmailFallback(EmailDto emailDto, Throwable t) {
        log.error("Failed to send email to {}", emailDto.getEmail(), t);
    }
}

package com.github.spjavaind300.notificationservice.servive;

import com.github.spjavaind300.notificationservice.exception.EmailException;
import com.github.spjavaind300.notificationservice.model.dto.EmailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    @Retryable(
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = {MessagingException.class, EmailException.class}
    )
    public void sendEmailAsync(EmailDto emailDto) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(emailDto.getEmail());
            helper.setSubject(emailDto.getSubject());
            helper.setText(emailDto.getBody(), true);

            mailSender.send(mimeMessage);
            log.info("Email sent to {}", emailDto.getEmail());

        } catch (MessagingException e) {
            log.error("Failed to send email to {}", emailDto.getEmail(), e);
            throw new EmailException("Failed to send email to: " + emailDto.getEmail(), e);
        }
    }

    public void sendEmailFallback(EmailDto emailDto, Throwable t) {
        log.error("Failed to send email to {}", emailDto.getEmail(), t);
    }

}

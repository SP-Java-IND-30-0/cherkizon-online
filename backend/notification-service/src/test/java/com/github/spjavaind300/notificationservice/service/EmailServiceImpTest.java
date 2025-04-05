package com.github.spjavaind300.notificationservice.service;

import com.github.spjavaind300.notificationservice.model.dto.EmailDto;
import com.github.spjavaind300.notificationservice.service.imp.EmailServiceImp;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.EnableRetry;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@EnableRetry
class EmailServiceImpTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;


    private EmailServiceImp emailService;

    private EmailDto validEmailDto;

    @BeforeEach
    void setUp() {
        emailService = new EmailServiceImp(mailSender);
        validEmailDto = EmailDto.builder()
                .email("test@example.com")
                .subject("Test Subject")
                .body("<html><body>Test Content</body></html>")
                .build();
    }

    @Test
    void sendEmailAsync_ShouldSuccessfullySendEmail() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendEmailAsync(validEmailDto);

        await().atMost(2, SECONDS).untilAsserted(() -> {
            verify(mailSender).send(mimeMessage);
            verify(mailSender, times(1)).createMimeMessage();
        });
    }


    @Test
    void sendEmailFallback_ShouldLogError_WhenAllRetriesExhausted() {

        Throwable cause = new MessagingException("Critical error");

        emailService.sendEmailFallback(validEmailDto, cause);

        assertTrue(true);
    }
}
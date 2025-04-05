package com.github.spjavaind300.notificationservice.service;

import com.github.spjavaind300.notificationservice.model.NotificationType;
import com.github.spjavaind300.notificationservice.model.dto.EmailDto;
import com.github.spjavaind300.notificationservice.model.dto.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    @Mock
    private EmailService emailService;

    @Mock
    private EmailStrategyFactory factory;

    @Mock
    private NotificationStrategy<Event> strategy;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;


    @BeforeEach
    void setUp() {
        lenient().doNothing().when(emailService).sendEmailAsync(any());

    }

    @Test
    void test_handleEvent_shouldProcessEventAndSendEmails() {
        Event event = new MockEvent();

        EmailDto emailDto = EmailDto.builder().email("test@example.com").subject(event.getSubject()).body("test body").build();

        when(factory.getStrategy(any(NotificationType.class))).thenReturn(strategy);
        when(strategy.prepareEmail(event)).thenReturn(List.of(emailDto));

        kafkaConsumerService.handleEvent(event);

        verify(factory, times(1)).getStrategy(any(NotificationType.class));
        verify(strategy, times(1)).prepareEmail(event);
        verify(emailService, times(1)).sendEmailAsync(any());

    }

    @Test
    void test_handleEvent_shouldProcessEventAndSendFewEmails() {
        Event event = new MockEvent();

        EmailDto emailDto1 = EmailDto.builder().email("test1@example.com").subject(event.getSubject()).body("test body").build();
        EmailDto emailDto2 = EmailDto.builder().email("test2@example.com").subject(event.getSubject()).body("test body").build();
        List<EmailDto> emails = List.of(emailDto1, emailDto2);

        when(factory.getStrategy(any(NotificationType.class))).thenReturn(strategy);
        when(strategy.prepareEmail(event)).thenReturn(emails);

        kafkaConsumerService.handleEvent(event);

        verify(factory, times(1)).getStrategy(any(NotificationType.class));
        verify(strategy, times(1)).prepareEmail(event);
        verify(emailService, times(emails.size())).sendEmailAsync(any());

    }

    @Test
    void test_handleEvent_whenEmptyEmailList_shouldNotSendEmails() {
        Event event = new MockEvent();

        when(factory.getStrategy(any(NotificationType.class))).thenReturn(strategy);
        when(strategy.prepareEmail(event)).thenReturn(Collections.emptyList());

        kafkaConsumerService.handleEvent(event);

        verify(factory, times(1)).getStrategy(any(NotificationType.class));
        verify(strategy, times(1)).prepareEmail(event);
        verify(emailService, never()).sendEmailAsync(any());

    }


    private static class MockEvent implements Event {

        @Override
        public NotificationType getNotificationType() {
            return NotificationType.COMMENT_CREATED;
        }

        @Override
        public String getSubject() {
            return "test subject";
        }
    }
}
package com.github.spjavaind300.service;

import com.github.spjavaind300.model.UserDeletedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    @Mock
    private AdService adService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Test
    void test_handleEvent() {

        UserDeletedEvent event = new UserDeletedEvent(1L);

        doNothing().when(adService).deleteAllByUserId(event.id());

        kafkaConsumerService.handleEvent(event);

        verify(adService).deleteAllByUserId(event.id());

    }
}
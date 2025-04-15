package com.github.spjavaind300.service.imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.spjavaind300.model.entity.OutboxEvent;
import com.github.spjavaind300.model.event.AdDeletedEvent;
import com.github.spjavaind300.model.event.AdEvent;
import com.github.spjavaind300.model.event.AdUpdatedEvent;
import com.github.spjavaind300.repository.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxEventServiceImpTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private KafkaTemplate<String, AdEvent> kafkaTemplate;

    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private OutboxEventServiceImp outboxEventService;

    private AdEvent deletedEvent, updatedEvent;

    @BeforeEach
    void setUp() {
        deletedEvent = new AdDeletedEvent(1);
        updatedEvent = new AdUpdatedEvent(1, "test", "AuthorFirstName", "AuthorLastName", "url");
    }


    @Test
    void test_saveOutboxEvent_whenSavingAdDeletedEvent_Success() {

        outboxEventService.saveOutboxEvent(deletedEvent);

        verify(outboxEventRepository).save(any(OutboxEvent.class));
    }

    @Test
    void test_saveOutboxEvent_whenSavingAdUpdatedEvent_Success() {

        outboxEventService.saveOutboxEvent(updatedEvent);

        verify(outboxEventRepository).save(any(OutboxEvent.class));
    }

    @Test
    void test_saveOutboxEvent_JsonProcessingException() throws JsonProcessingException {
        AdEvent event = new AdDeletedEvent(1);
        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("Error") {
        });

        assertThrows(IllegalArgumentException.class, () -> outboxEventService.saveOutboxEvent(event));

        verify(outboxEventRepository, never()).save(any());
    }

    @Test
    void test_processOutbox_Success() {

        OutboxEvent event = new OutboxEvent(1, "123", "adv.deleted", "{\"id\":\"123\"}", false);
        when(outboxEventRepository.findAllByProcessedIsFalse()).thenReturn(List.of(event));
        when(kafkaTemplate.send(eq("adv.deleted"), eq("123"), any(AdEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        outboxEventService.processOutbox();

        verify(kafkaTemplate).send(eq("adv.deleted"), eq("123"), any(AdEvent.class));
        verify(outboxEventRepository).delete(event);
    }

    @Test
    void test_processOutbox_JsonProcessingException() {
        OutboxEvent event = new OutboxEvent(1, "123", "test-topic", "invalid-json", false);
        when(outboxEventRepository.findAllByProcessedIsFalse()).thenReturn(List.of(event));

        outboxEventService.processOutbox();

        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
        verify(outboxEventRepository, never()).delete(event);
    }

    @Test
    void test_processOutbox_KafkaSendFailed() {
        OutboxEvent event = new OutboxEvent(1, "123", "adv.deleted", "{\"id\":\"123\"}", false);
        when(outboxEventRepository.findAllByProcessedIsFalse()).thenReturn(List.of(event));
        when(kafkaTemplate.send(anyString(), anyString(), any(AdEvent.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Kafka error")));

        outboxEventService.processOutbox();

        verify(kafkaTemplate).send(eq("adv.deleted"), eq("123"), any(AdEvent.class));
        verify(outboxEventRepository, never()).delete(event);
    }
}
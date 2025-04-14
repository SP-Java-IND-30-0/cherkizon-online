package com.github.spjavaind300.service.imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.spjavaind300.model.entity.OutboxEvent;
import com.github.spjavaind300.model.event.AdDeletedEvent;
import com.github.spjavaind300.model.event.AdEvent;
import com.github.spjavaind300.model.event.AdUpdatedEvent;
import com.github.spjavaind300.repository.OutboxEventRepository;
import com.github.spjavaind300.service.OutboxEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OutboxEventServiceImp implements OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, AdEvent> kafkaTemplate;
    private final Map<String, Class<? extends AdEvent>> eventMap = new HashMap<>();

    public OutboxEventServiceImp(
            OutboxEventRepository outboxEventRepository,
            ObjectMapper objectMapper,
            KafkaTemplate<String, AdEvent> kafkaTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;

        eventMap.put(AdDeletedEvent.TOPIC, AdDeletedEvent.class);
        eventMap.put(AdUpdatedEvent.TOPIC, AdUpdatedEvent.class);
    }


    @Override
    public void saveOutboxEvent(AdEvent event) {
        try {
            outboxEventRepository.save(new OutboxEvent(0, event.eventId(), event.topic(), getEventJson(event), false));
        } catch (JsonProcessingException e) {
            log.error("JSON parse failed for event {}. Event: {}. Error: {}",
                    event.eventId(), event, e.getMessage());
            throw new IllegalArgumentException("JSON parse failed for event %s. Event: %s. Error: %s"
                    .formatted(event.eventId(), event, e.getMessage()));
        }
    }


    @Scheduled(fixedDelay = 5000)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOutbox() {
        outboxEventRepository.findAllByProcessedIsFalse().forEach(event -> {
            try {
                AdEvent adEvent = objectMapper.readValue(event.getPayload(), eventMap.get(event.getEventType()));
                kafkaTemplate.send(event.getEventType(), event.getEventId(), adEvent)
                        .thenAccept(result -> outboxEventRepository.delete(event))
                        .exceptionally(ex -> {
                            log.error("Kafka send failed for event {}. Error: {}", event.getEventId(), ex.getMessage());
                            return null;
                        });
            } catch (IllegalArgumentException | JsonProcessingException e) {
                log.error("JSON parse failed for event {}. Payload: {}. Error: {}",
                        event.getEventId(), event.getPayload(), e.getMessage());
            }
        });
    }

    private String getEventJson(AdEvent event) throws JsonProcessingException {
        return objectMapper.writeValueAsString(event);
    }


}

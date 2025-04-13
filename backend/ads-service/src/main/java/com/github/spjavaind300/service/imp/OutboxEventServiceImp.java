package com.github.spjavaind300.service.imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.spjavaind300.model.entity.OutboxEvent;
import com.github.spjavaind300.model.event.AdEvent;
import com.github.spjavaind300.repository.OutboxEventRepository;
import com.github.spjavaind300.service.OutboxEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
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
            KafkaTemplate<String, AdEvent> kafkaTemplate,
            @Autowired List<AdEvent> adEvents) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
        adEvents.forEach(adEvent -> eventMap.put(adEvent.topic(), adEvent.getClass()));
    }


    @Override
    public void saveOutboxEvent(AdEvent event) {
        try {
            outboxEventRepository.save(new OutboxEvent(0, event.eventId(), event.topic(), getEventJson(event), false));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage()); //TODO: обработать ошибку
        }
    }


    @Scheduled(fixedDelay = 5000)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOutbox() {
        outboxEventRepository.findAllByProcessedIsFalse().forEach(event -> {
            try {
                AdEvent adEvent = objectMapper.readValue(event.getPayload(), eventMap.get(event.getEventType()));
                kafkaTemplate.send(event.getEventType(), event.getEventId(), adEvent)
                        .thenAccept(result -> outboxEventRepository.delete(event));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage()); //TODO: обработать ошибку
            }
        });
    }

    private String getEventJson(AdEvent event) throws JsonProcessingException {
        return objectMapper.writeValueAsString(event);
    }


}

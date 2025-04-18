package com.github.spjavaind300.commentsservice.kafka;

import com.github.spjavaind300.commentsservice.kafka.dto.CommentCreatedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<String, CommentCreatedEvent> commentCreatedKafkaTemplate(
            ProducerFactory<String, CommentCreatedEvent> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }
}
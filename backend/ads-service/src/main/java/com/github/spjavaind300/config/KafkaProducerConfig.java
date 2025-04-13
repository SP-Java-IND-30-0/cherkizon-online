package com.github.spjavaind300.config;

import com.github.spjavaind300.model.event.AdEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<String, AdEvent> kafkaTemplate(
            ProducerFactory<String, AdEvent> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }
}

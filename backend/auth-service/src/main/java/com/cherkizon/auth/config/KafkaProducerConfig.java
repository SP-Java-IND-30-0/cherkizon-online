package com.cherkizon.auth.config;

import com.cherkizon.auth.dto.event.UserEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<String, UserEvent> kafkaTemplate(
            ProducerFactory<String, UserEvent> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }
}

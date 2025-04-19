package com.github.spjavaind300.profileservice.config;

import com.github.spjavaind300.profileservice.dto.event.UserDeletedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<String, UserDeletedEvent> kafkaTemplate(
            ProducerFactory<String, UserDeletedEvent> pf
    ) {
        return new KafkaTemplate<>(pf);
    }
}

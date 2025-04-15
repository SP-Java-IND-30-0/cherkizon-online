package com.github.spjavaind300.config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;

import static org.mockito.Mockito.mock;

@TestConfiguration
@ExtendWith(MockitoExtension.class)
public class TestKafkaConfig {

    @Bean
    @Primary
    public KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry() {
        return mock(KafkaListenerEndpointRegistry.class);
    }

    @Bean
    @Primary
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return mock(KafkaTemplate.class);
    }

    @Bean
    public CommonErrorHandler kafkaErrorHandler() {
        return mock(CommonErrorHandler.class);
    }
}

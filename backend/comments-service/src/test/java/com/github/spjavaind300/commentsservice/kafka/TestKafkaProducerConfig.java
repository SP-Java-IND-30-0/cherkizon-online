package com.github.spjavaind300.commentsservice.kafka;

import com.github.spjavaind300.commentsservice.kafka.dto.AdDeletedEvent;
import com.github.spjavaind300.commentsservice.kafka.dto.CommentCreatedEvent;
import com.github.spjavaind300.commentsservice.kafka.dto.UserDeletedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@TestConfiguration
@Profile("test")
public class TestKafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> baseProducerConfigs() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return config;
    }

    @Bean
    public ProducerFactory<String, UserDeletedEvent> userDeletedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseProducerConfigs());
    }

    @Bean
    public KafkaTemplate<String, UserDeletedEvent> userDeletedKafkaTemplate() {
        return new KafkaTemplate<>(userDeletedProducerFactory());
    }

    @Bean
    public ProducerFactory<String, AdDeletedEvent> adDeletedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseProducerConfigs());
    }

    @Bean
    public KafkaTemplate<String, AdDeletedEvent> adDeletedKafkaTemplate() {
        return new KafkaTemplate<>(adDeletedProducerFactory());
    }

    @Bean
    public ProducerFactory<String, CommentCreatedEvent> commentCreatedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseProducerConfigs());
    }

    @Bean
    @Primary
    public KafkaTemplate<String, CommentCreatedEvent> testCommentCreatedKafkaTemplate() {
        return new KafkaTemplate<>(commentCreatedProducerFactory());
    }
}
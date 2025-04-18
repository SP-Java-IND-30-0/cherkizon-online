package com.github.spjavaind300.commentsservice.kafka;

import com.github.spjavaind300.commentsservice.kafka.dto.AdDeletedEvent;
import com.github.spjavaind300.commentsservice.kafka.dto.UserDeletedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> baseConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    @Bean
    public ConsumerFactory<String, UserDeletedEvent> userDeletedConsumerFactory() {
        JsonDeserializer<UserDeletedEvent> deserializer = new JsonDeserializer<>(UserDeletedEvent.class);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                baseConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserDeletedEvent> userDeletedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserDeletedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userDeletedConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, AdDeletedEvent> adDeletedConsumerFactory() {
        JsonDeserializer<AdDeletedEvent> deserializer = new JsonDeserializer<>(AdDeletedEvent.class);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                baseConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AdDeletedEvent> adDeletedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AdDeletedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(adDeletedConsumerFactory());
        return factory;
    }
}
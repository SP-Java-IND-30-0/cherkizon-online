package com.github.spjavaind300.commentsservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic commentCreatedTopic() {
        return TopicBuilder.name("comment.created")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
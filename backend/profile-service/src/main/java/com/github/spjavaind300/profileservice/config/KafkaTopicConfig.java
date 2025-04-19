package com.github.spjavaind300.profileservice.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic authUserCreatedTopic() {
        return TopicBuilder
                .name("auth.user.created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic profileUserDeletedTopic() {
        return TopicBuilder
                .name("profile.user.deleted")
                .partitions(3)
                .replicas(1)
                .build();
    }
}


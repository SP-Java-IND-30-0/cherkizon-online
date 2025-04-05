package com.github.spjavaind300.notificationservice;

import com.github.spjavaind300.notificationservice.config.TestKafkaConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = {KafkaAutoConfiguration.class})
@Import(TestKafkaConfig.class)
class NotificationServiceApplicationTests {

    @Test
    void contextLoads() {
        assertTrue(true);
    }

}

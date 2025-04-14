package com.github.spjavaind300;

import com.github.spjavaind300.config.TestKafkaConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@EnableAutoConfiguration(exclude = {KafkaAutoConfiguration.class})
@Import(TestKafkaConfig.class)
public class AdsServiceApplicationTests {
    @Test
    void contextLoads() {
    }
}

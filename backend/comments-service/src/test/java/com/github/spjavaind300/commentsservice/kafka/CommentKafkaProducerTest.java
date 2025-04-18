package com.github.spjavaind300.commentsservice.kafka;

import com.github.spjavaind300.commentsservice.kafka.dto.CommentCreatedEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import com.github.spjavaind300.commentsservice.model.Comment;
import com.github.spjavaind300.commentsservice.repository.CommentRepository;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@EmbeddedKafka(partitions = 1, topics = {"comment.created"})
@TestPropertySource(properties = {
        "spring.kafka.consumer.auto-offset-reset=earliest"
})
class CommentKafkaProducerTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("db_test")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("init_schema.sql");

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private CommentRepository commentRepository;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private final int TEST_AD_ID = 42;
    private final long TEST_AUTHOR_ID = 123L;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
    }

    @AfterEach
    void cleanup() {
        commentRepository.deleteAll();
    }

    @Test
    @DisplayName("Отправка события CommentCreatedEvent в Kafka")
    void testSendCommentCreatedEvent() {
        Comment comment = Comment.builder()
                .adId(TEST_AD_ID)
                .authorId(TEST_AUTHOR_ID)
                .text("Новый комментарий")
                .createdAt(Instant.now())
                .build();
        commentRepository.save(comment);

        CommentCreatedEvent event = new CommentCreatedEvent(
                comment.getId(),
                TEST_AD_ID,
                TEST_AUTHOR_ID,
                comment.getText(),
                comment.getCreatedAt()
        );

        kafkaTemplate.send("comment.created", String.valueOf(comment.getId()), event);

        CommentCreatedEvent received = receiveMessage();

        assertThat(received).isNotNull();
        assertThat(received.getCommentId()).isEqualTo(comment.getId());
        assertThat(received.getAdId()).isEqualTo(TEST_AD_ID);
        assertThat(received.getText()).isEqualTo("Новый комментарий");
    }

    private CommentCreatedEvent receiveMessage() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-" + UUID.randomUUID());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Consumer<String, CommentCreatedEvent> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("comment.created"));

        ConsumerRecord<String, CommentCreatedEvent> record = null;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 5000) {
            ConsumerRecords<String, CommentCreatedEvent> records = consumer.poll(Duration.ofMillis(100));
            if (!records.isEmpty()) {
                record = records.iterator().next();
                break;
            }
        }
        consumer.close();
        return record != null ? record.value() : null;
    }
}
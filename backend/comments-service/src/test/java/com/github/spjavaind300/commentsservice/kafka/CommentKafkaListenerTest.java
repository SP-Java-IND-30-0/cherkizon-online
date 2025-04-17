package com.github.spjavaind300.commentsservice.kafka;

import com.github.spjavaind300.commentsservice.kafka.dto.AdDeletedEvent;
import com.github.spjavaind300.commentsservice.kafka.dto.UserDeletedEvent;
import com.github.spjavaind300.commentsservice.model.Comment;
import com.github.spjavaind300.commentsservice.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@EmbeddedKafka(partitions = 1, topics = {"profile.user.deleted", "adv.deleted"})
@TestPropertySource(properties = {
        "spring.kafka.consumer.group-id=comment-test-listener-${random.uuid}"
})
class CommentKafkaListenerTest {

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

    private final long TEST_AUTHOR_ID = 123L;
    private final int TEST_AD_ID = 42;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();

        commentRepository.saveAll(List.of(
                Comment.builder()
                        .authorId(TEST_AUTHOR_ID)
                        .adId(TEST_AD_ID)
                        .text("Комментарий 1")
                        .createdAt(Instant.now())
                        .build(),
                Comment.builder()
                        .authorId(TEST_AUTHOR_ID)
                        .adId(99)
                        .text("Комментарий 2")
                        .createdAt(Instant.now())
                        .build()
        ));
    }

    @AfterEach
    void cleanup() {
        commentRepository.deleteAll();
    }

    @Test
    @DisplayName("Удаление комментариев по authorId при событии UserDeletedEvent")
    void testHandleUserDeletedEvent_shouldDeleteAllCommentsByAuthorId() {
        kafkaTemplate.send("profile.user.deleted", new UserDeletedEvent(TEST_AUTHOR_ID));

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<Comment> comments = commentRepository.findAll();
            assertThat(comments).isEmpty();
        });
    }

    @Test
    @DisplayName("Удаление комментариев по adId при событии AdDeletedEvent")
    void testHandleAdDeletedEvent_shouldDeleteAllCommentsByAdId() {
        kafkaTemplate.send("adv.deleted", new AdDeletedEvent(TEST_AD_ID));

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<Comment> comments = commentRepository.findAll();
            assertThat(comments)
                    .hasSize(1)
                    .allMatch(comment -> comment.getAdId() == 99);
        });
    }
}
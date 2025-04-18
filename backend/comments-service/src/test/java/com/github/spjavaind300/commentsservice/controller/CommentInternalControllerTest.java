package com.github.spjavaind300.commentsservice.controller;

import com.github.spjavaind300.commentsservice.model.Comment;
import com.github.spjavaind300.commentsservice.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestPropertySource(properties = {"JWT_SECRET_KEY=test-secret-key"})
class CommentInternalControllerTest {

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17-alpine")
                    .withDatabaseName("db_test")
                    .withUsername("test")
                    .withPassword("test")
                    .withInitScript("init_schema.sql");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
    }

    @Test
    @DisplayName("Получение авторов комментариев — комментарии найдены")
    void getCommentAuthors_shouldReturnSetOfAuthorIds() throws Exception {
        int adId = 1;

        commentRepository.saveAll(List.of(
                Comment.builder().adId(adId).authorId(100L).text("text1").createdAt(Instant.now()).build(),
                Comment.builder().adId(adId).authorId(101L).text("text2").createdAt(Instant.now()).build(),
                Comment.builder().adId(adId).authorId(100L).text("text3").createdAt(Instant.now()).build()
        ));

        mockMvc.perform(get("/internal/comment/{adId}/authors", adId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$").value(Matchers.containsInAnyOrder(100, 101)));
    }

    @Test
    @DisplayName("Получение авторов комментариев — комментариев нет")
    void getCommentAuthors_shouldReturnEmptySet_whenNoComments() throws Exception {
        int adId = 999;

        mockMvc.perform(get("/internal/comment/{adId}/authors", adId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
package com.github.spjavaind300.commentsservice.controller;

import com.github.spjavaind300.commentsservice.cache.ProfileCacheService;
import com.github.spjavaind300.commentsservice.dto.ProfileDto;
import com.github.spjavaind300.commentsservice.dto.Role;
import com.github.spjavaind300.commentsservice.dto.UserContext;
import com.github.spjavaind300.commentsservice.feing.AdsFeignClientInternal;
import com.github.spjavaind300.commentsservice.model.Comment;
import com.github.spjavaind300.commentsservice.repository.CommentRepository;
import com.github.spjavaind300.commentsservice.service.JwtUtils;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class CommentControllerTest {

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17-alpine")
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test")
                    .withInitScript("init_schema.sql");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentRepository commentRepository;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private ProfileCacheService profileCacheService;

    @MockitoBean
    private AdsFeignClientInternal adsFeignClientInternal;

    private final ProfileDto testProfile = new ProfileDto();

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();

        testProfile.setAuthorId(1L);
        testProfile.setAuthorFirstName("Иван");
        testProfile.setAuthorImage("avatar.jpg");

        when(jwtUtils.getUserContext()).thenReturn(new UserContext(1L, Role.USER));
        when(profileCacheService.getProfile(1L)).thenReturn(testProfile);
        doNothing().when(adsFeignClientInternal).checkAdExists(anyInt());
    }

    @Test
    @WithMockUser
    @DisplayName("Получение комментариев по объявлению — успешно")
    void getComments_shouldReturnList() throws Exception {
        int adId = 1;

        commentRepository.save(Comment.builder()
                .adId(adId)
                .authorId(1L)
                .text("Комментарий")
                .createdAt(Instant.now())
                .build());

        mockMvc.perform(get("/ads/{id}/comments", adId)
                        .header("Authorization", "Bearer mock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.length()").value(1));
    }

    @Test
    @DisplayName("Получение комментариев — неавторизованный пользователь")
    void getComments_shouldReturn401() throws Exception {
        mockMvc.perform(get("/ads/{id}/comments", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("Добавление комментария — успешно")
    void addComment_shouldSucceed() throws Exception {
        int adId = 1;
        String body = """
                { "text": "Новый комментарий" }
                """;

        mockMvc.perform(post("/ads/{id}/comments", adId)
                        .with(csrf())
                        .header("Authorization", "Bearer mock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Новый комментарий"));
    }

    @Test
    @WithMockUser
    @DisplayName("Добавление комментария — невалидное тело")
    void addComment_shouldFailValidation() throws Exception {
        int adId = 1;

        mockMvc.perform(post("/ads/{id}/comments", adId)
                        .with(csrf())
                        .header("Authorization", "Bearer mock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Обновление комментария — успешно")
    void updateComment_shouldSucceed() throws Exception {
        int adId = 1;
        Comment saved = commentRepository.save(Comment.builder()
                .adId(adId)
                .authorId(1L)
                .text("Старый текст")
                .createdAt(Instant.now())
                .build());

        String updatedText = """
                { "text": "Обновлённый текст" }
                """;

        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", adId, saved.getId())
                        .with(csrf())
                        .header("Authorization", "Bearer mock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedText))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Обновлённый текст"));
    }

    @Test
    @WithMockUser
    @DisplayName("Обновление чужого комментария — запрет")
    void updateComment_shouldReturnForbidden() throws Exception {
        int adId = 1;
        var saved = commentRepository.save(Comment.builder()
                .adId(adId)
                .authorId(999L)
                .text("Чужой")
                .createdAt(Instant.now())
                .build());

        String updatedText = """
            { "text": "Не положено" }
            """;

        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", adId, saved.getId())
                        .with(csrf())
                        .header("Authorization", "Bearer mock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedText))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    @DisplayName("Удаление комментария — успешно")
    void deleteComment_shouldSucceed() throws Exception {
        int adId = 1;
        Comment comment = commentRepository.save(Comment.builder()
                .adId(adId)
                .authorId(1L)
                .text("Для удаления")
                .createdAt(Instant.now())
                .build());

        mockMvc.perform(delete("/ads/{adId}/comments/{commentId}", adId, comment.getId())
                        .with(csrf())
                        .header("Authorization", "Bearer mock"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("Удаление комментария — не найден")
    void deleteComment_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/ads/{adId}/comments/{commentId}", 1, 999)
                        .with(csrf())
                        .header("Authorization", "Bearer mock"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Удаление чужого комментария — запрет")
    void deleteComment_shouldReturnForbidden() throws Exception {
        int adId = 1;

        Comment saved = commentRepository.save(Comment.builder()
                .adId(adId)
                .authorId(999L)
                .text("Чужой")
                .createdAt(Instant.now())
                .build());

        mockMvc.perform(delete("/ads/{adId}/comments/{commentId}", adId, saved.getId())
                        .with(csrf())
                        .header("Authorization", "Bearer mock"))
                .andExpect(status().isForbidden());
    }
}
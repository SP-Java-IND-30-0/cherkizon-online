package com.github.spjavaind300.commentsservice.service;

import com.github.spjavaind300.commentsservice.client.ProfileFeignClientInternal;
import com.github.spjavaind300.commentsservice.component.CommentProfileCache;
import com.github.spjavaind300.commentsservice.dto.CommentDto;
import com.github.spjavaind300.commentsservice.dto.ProfileDto;
import com.github.spjavaind300.commentsservice.exception.BadRequestException;
import com.github.spjavaind300.commentsservice.exception.ForbiddenException;
import com.github.spjavaind300.commentsservice.mapper.CommentMapper;
import com.github.spjavaind300.commentsservice.model.Comment;
import com.github.spjavaind300.commentsservice.repository.CommentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
public class CommentServiceTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine"))
            .withDatabaseName("db_test")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("init_schema.sql");

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentMapper commentMapper;

    @MockitoBean
    private ProfileFeignClientInternal profileFeignClient;

    @MockitoBean
    private CommentProfileCache commentProfileCache;

    private CommentServiceImpl commentService;

    private ProfileDto testProfile;

    @BeforeEach
    void setUp() {
        commentService = new CommentServiceImpl(commentRepository, profileFeignClient, commentMapper, commentProfileCache);

        testProfile = new ProfileDto();
        testProfile.setAuthorId(1L);
        testProfile.setAuthorFirstName("Иван");
        testProfile.setAuthorImage("avatar.jpg");

    }

    @AfterEach
    void tearDown() {
        commentRepository.deleteAll();
    }

    @Test
    @DisplayName("Проверка получения комментариев для объявления, когда комментарии существуют - должен вернуть список комментариев")
    void test_getCommentsForAd_whenCommentsExist_returnsListOfComments() {
        Comment comment = Comment.builder()
                .adId(1)
                .text("Первый комментарий")
                .createdAt(Instant.now())
                .build();

        Comment savedComment = commentRepository.save(comment);
        when(commentProfileCache.get(savedComment.getId())).thenReturn(testProfile);

        List<CommentDto> result = commentService.getCommentsForAd(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Первый комментарий", result.getFirst().getText());
    }

    @Test
    @DisplayName("Проверка получения комментариев для объявления, когда комментариев нет - должен вернуть пустой список")
    void test_getCommentsForAd_whenNoComments_returnsEmptyList() {
        List<CommentDto> result = commentService.getCommentsForAd(1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Проверка добавления комментария - должен добавить новый комментарий и вернуть CommentDto")
    void test_addComment_success() {
        when(profileFeignClient.getCurrentProfileInternal()).thenReturn(testProfile);

        CommentDto result = commentService.addComment(1, "Новый комментарий");

        assertNotNull(result);
        assertEquals("Новый комментарий", result.getText());

        assertEquals(testProfile.getAuthorId(), result.getAuthor());
        assertEquals(testProfile.getAuthorFirstName(), result.getAuthorFirstName());
        assertEquals(testProfile.getAuthorImage(), result.getAuthorImage());
    }

    @Test
    @DisplayName("Проверка добавления комментария, когда профиль не найден - должен выбросить BadRequestException")
    void test_addComment_whenProfileNotFound_throwsBadRequestException() {
        when(profileFeignClient.getCurrentProfileInternal()).thenReturn(null);

        assertThrows(BadRequestException.class, () -> commentService.addComment(1, "Новый комментарий"));
    }

    @Test
    @DisplayName("Проверка обновления комментария - должен обновить существующий комментарий и вернуть обновленный CommentDto")
    void test_updateComment_success() {
        Comment comment = Comment.builder()
                .adId(1)
                .text("Старый комментарий")
                .createdAt(Instant.now())
                .build();

        Comment savedComment = commentRepository.save(comment);

        when(profileFeignClient.getCurrentProfileInternal()).thenReturn(testProfile);
        when(commentProfileCache.get(savedComment.getId())).thenReturn(testProfile);

        CommentDto result = commentService.updateComment(1, savedComment.getId(), "Обновленный комментарий");

        assertNotNull(result);
        assertEquals("Обновленный комментарий", result.getText());
    }

    @Test
    @DisplayName("Проверка обновления комментария, когда пользователь не является автором - должен выбросить ForbiddenException")
    void test_updateComment_whenNotAuthor_throwsForbiddenException() {
        Comment comment = Comment.builder()
                .adId(1)
                .text("Старый комментарий")
                .createdAt(Instant.now())
                .build();

        Comment savedComment = commentRepository.save(comment);

        ProfileDto anotherProfile = new ProfileDto();
        anotherProfile.setAuthorId(999L);
        when(profileFeignClient.getCurrentProfileInternal()).thenReturn(anotherProfile);
        when(commentProfileCache.get(savedComment.getId())).thenReturn(testProfile);

        assertThrows(ForbiddenException.class,
                () -> commentService.updateComment(1, savedComment.getId(), "Попытка редактирования"));
    }

    @Test
    @DisplayName("Проверка удаления комментария - должен удалить комментарий и удалить его из кэша")
    void test_deleteComment_success() {
        Comment comment = Comment.builder()
                .adId(1)
                .text("Удалить этот комментарий")
                .createdAt(Instant.now())
                .build();

        Comment savedComment = commentRepository.save(comment);
        when(profileFeignClient.getCurrentProfileInternal()).thenReturn(testProfile);
        when(commentProfileCache.get(savedComment.getId())).thenReturn(testProfile);

        commentService.deleteComment(1, savedComment.getId());

        assertFalse(commentRepository.findById(savedComment.getId()).isPresent());
        verify(commentProfileCache).remove(savedComment.getId());
    }

    @Test
    @DisplayName("Проверка удаления комментария, когда пользователь не является автором - должен выбросить ForbiddenException")
    void test_deleteComment_whenNotAuthor_throwsForbiddenException() {
        Comment comment = Comment.builder()
                .adId(1)
                .text("Удалить этот комментарий")
                .createdAt(Instant.now())
                .build();

        Comment savedComment = commentRepository.save(comment);

        ProfileDto anotherProfile = new ProfileDto();
        anotherProfile.setAuthorId(999L);
        when(profileFeignClient.getCurrentProfileInternal()).thenReturn(anotherProfile);
        when(commentProfileCache.get(savedComment.getId())).thenReturn(testProfile);

        assertThrows(ForbiddenException.class,
                () -> commentService.deleteComment(1, savedComment.getId()));
    }

    @Test
    @DisplayName("Проверка получения комментариев для объявления, когда в БД нет комментариев - должен вернуть пустой список")
    void test_getCommentsForAd_whenNoCommentsInDb_returnsEmptyList() {
        List<CommentDto> result = commentService.getCommentsForAd(999);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
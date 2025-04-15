package com.github.spjavaind300.commentsservice.service;

import com.github.spjavaind300.commentsservice.client.ProfileFeignClientInternal;
import com.github.spjavaind300.commentsservice.component.AuthorProfileCache;
import com.github.spjavaind300.commentsservice.dto.CommentTextDto;
import com.github.spjavaind300.commentsservice.dto.ProfileDto;
import com.github.spjavaind300.commentsservice.dto.Role;
import com.github.spjavaind300.commentsservice.dto.UserContext;
import com.github.spjavaind300.commentsservice.exception.ForbiddenException;
import com.github.spjavaind300.commentsservice.exception.NotFoundException;
import com.github.spjavaind300.commentsservice.mapper.CommentMapper;
import com.github.spjavaind300.commentsservice.model.Comment;
import com.github.spjavaind300.commentsservice.repository.CommentRepository;
import jakarta.servlet.http.HttpServletRequest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

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
    private AuthorProfileCache authorProfileCache;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private ProfileFeignClientInternal profileFeignClient;

    @Autowired
    private HttpServletRequest request;

    private CommentServiceImpl commentService;

    private ProfileDto testProfile;

    @BeforeEach
    void setUp() {
        commentService = new CommentServiceImpl(
                commentRepository,
                profileFeignClient,
                commentMapper,
                authorProfileCache,
                jwtUtils,
                request
        );

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
    @DisplayName("Получение комментариев для объявления")
    void test_getCommentsForAd_returnsList() {
        Comment comment = commentRepository.save(Comment.builder()
                .adId(1)
                .authorId(testProfile.getAuthorId())
                .text("Комментарий")
                .createdAt(Instant.now())
                .build());

        when(authorProfileCache.get(comment.getAuthorId())).thenReturn(testProfile);

        var result = commentService.getCommentsForAd(1, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.get("results").size());
        assertEquals("Комментарий", result.get("results").getFirst().getText());
    }

    @Test
    @DisplayName("Добавление комментария — успешное")
    void test_addComment_success() {
        CommentTextDto commentTextDto = new CommentTextDto();
        commentTextDto.setText("Комментарий");

        commentRepository.save(Comment.builder()
                .adId(1)
                .authorId(testProfile.getAuthorId())
                .text("Старый комментарий")
                .createdAt(Instant.now())
                .build());

        when(authorProfileCache.get(testProfile.getAuthorId())).thenReturn(testProfile);
        when(jwtUtils.getUserContext(request)).thenReturn(new UserContext(testProfile.getAuthorId(), Role.USER));

        var result = commentService.addComment(1, commentTextDto);

        assertNotNull(result);
        assertEquals("Комментарий", result.getText());
    }

    @Test
    @DisplayName("Добавление комментария — профиль из feign")
    void test_addComment_profileFetchedViaFeign() {
        CommentTextDto commentTextDto = new CommentTextDto();
        commentTextDto.setText("Новый комментарий");

        commentRepository.save(Comment.builder()
                .adId(1)
                .authorId(testProfile.getAuthorId())
                .text("Ранее добавленный")
                .createdAt(Instant.now())
                .build());

        when(authorProfileCache.get(testProfile.getAuthorId())).thenReturn(null);
        when(profileFeignClient.getProfileByIdInternal(testProfile.getAuthorId())).thenReturn(testProfile);
        when(jwtUtils.getUserContext(request)).thenReturn(new UserContext(testProfile.getAuthorId(), Role.USER));

        var result = commentService.addComment(1, commentTextDto);

        assertNotNull(result);
        assertEquals("Новый комментарий", result.getText());
    }

    @Test
    @DisplayName("Добавление комментария — юзер контекст отсутствует → NotFoundException")
    void test_addComment_noUserContext_throwsNotFound() {
        when(jwtUtils.getUserContext(request)).thenReturn(null);

        CommentTextDto commentTextDto = new CommentTextDto();
        commentTextDto.setText("Ошибка");

        assertThrows(NotFoundException.class, () -> commentService.addComment(1, commentTextDto));
    }

    @Test
    @DisplayName("Обновление комментария — успешное")
    void test_updateComment_success() {
        Comment saved = commentRepository.save(Comment.builder()
                .adId(1)
                .authorId(testProfile.getAuthorId())
                .text("Старый")
                .createdAt(Instant.now())
                .build());

        CommentTextDto commentTextDto = new CommentTextDto();
        commentTextDto.setText("Новый");

        when(jwtUtils.getUserContext(request)).thenReturn(new UserContext(testProfile.getAuthorId(), Role.USER));
        when(authorProfileCache.get(testProfile.getAuthorId())).thenReturn(testProfile);

        var result = commentService.updateComment(1, saved.getId(), commentTextDto);

        assertEquals("Новый", result.getText());
    }

    @Test
    @DisplayName("Обновление комментария — не автор")
    void test_updateComment_notAuthor_forbidden() {
        Comment saved = commentRepository.save(Comment.builder()
                .adId(1)
                .authorId(1L)
                .text("Тест")
                .createdAt(Instant.now())
                .build());

        ProfileDto other = new ProfileDto();
        other.setAuthorId(2L);
        other.setAuthorFirstName("Нет доступа");

        CommentTextDto commentTextDto = new CommentTextDto();
        commentTextDto.setText("Обновить");

        when(jwtUtils.getUserContext(request)).thenReturn(new UserContext(2L, Role.USER));
        when(authorProfileCache.get(2L)).thenReturn(other);

        assertThrows(ForbiddenException.class, () -> commentService.updateComment(1, saved.getId(), commentTextDto));
    }

    @Test
    @DisplayName("Обновление комментария — не найден")
    void test_updateComment_notFound() {
        when(jwtUtils.getUserContext(request)).thenReturn(new UserContext(1L, Role.USER));

        CommentTextDto commentTextDto = new CommentTextDto();
        commentTextDto.setText("Ничего");

        assertThrows(NotFoundException.class, () -> commentService.updateComment(1, 999, commentTextDto));
    }

    @Test
    @DisplayName("Удаление комментария — автор")
    void test_deleteComment_author() {
        Comment saved = commentRepository.save(Comment.builder()
                .adId(1)
                .authorId(testProfile.getAuthorId())
                .text("Удалить")
                .createdAt(Instant.now())
                .build());

        when(jwtUtils.getUserContext(request)).thenReturn(new UserContext(testProfile.getAuthorId(), Role.USER));
        when(authorProfileCache.get(testProfile.getAuthorId())).thenReturn(testProfile);

        commentService.deleteComment(1, saved.getId());

        assertTrue(commentRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Удаление комментария — не автор, не админ")
    void test_deleteComment_notAuthor_forbidden() {
        Comment saved = commentRepository.save(Comment.builder()
                .adId(1)
                .authorId(1L)
                .text("Не удалено")
                .createdAt(Instant.now())
                .build());

        ProfileDto other = new ProfileDto();
        other.setAuthorId(2L);
        other.setAuthorFirstName("Нет доступа");

        when(jwtUtils.getUserContext(request)).thenReturn(new UserContext(2L, Role.USER));
        when(authorProfileCache.get(2L)).thenReturn(other);

        assertThrows(ForbiddenException.class, () -> commentService.deleteComment(1, saved.getId()));
    }

    @Test
    @DisplayName("Удаление комментария — админ")
    void test_deleteComment_admin() {
        Comment saved = commentRepository.save(Comment.builder()
                .adId(1)
                .authorId(1L)
                .text("Удалить админом")
                .createdAt(Instant.now())
                .build());

        ProfileDto admin = new ProfileDto();
        admin.setAuthorId(99L);
        admin.setAuthorFirstName("Администратор");

        when(jwtUtils.getUserContext(request)).thenReturn(new UserContext(99L, Role.ADMIN));
        when(authorProfileCache.get(99L)).thenReturn(admin);

        commentService.deleteComment(1, saved.getId());

        assertTrue(commentRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Удаление комментария — не найден")
    void test_deleteComment_notFound() {
        when(jwtUtils.getUserContext(request)).thenReturn(new UserContext(1L, Role.USER));
        assertThrows(NotFoundException.class, () -> commentService.deleteComment(1, 404));
    }

    @Test
    @DisplayName("Профиль добавляется в кэш при добавлении комментария")
    void test_addComment_putsProfileInCache() {
        CommentTextDto commentTextDto = new CommentTextDto();
        commentTextDto.setText("Комментарий");

        commentRepository.save(Comment.builder()
                .adId(1)
                .authorId(testProfile.getAuthorId())
                .text("Комментарий-носитель объявления")
                .createdAt(Instant.now())
                .build());

        when(jwtUtils.getUserContext(request)).thenReturn(new UserContext(testProfile.getAuthorId(), Role.USER));
        when(authorProfileCache.get(testProfile.getAuthorId())).thenReturn(null);
        when(profileFeignClient.getProfileByIdInternal(testProfile.getAuthorId())).thenReturn(testProfile);

        commentService.addComment(1, commentTextDto);

        verify(authorProfileCache, times(2)).put(testProfile.getAuthorId(), testProfile);
    }
}
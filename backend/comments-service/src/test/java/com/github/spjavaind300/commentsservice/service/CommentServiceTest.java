package com.github.spjavaind300.commentsservice.service;

import com.github.spjavaind300.commentsservice.cache.AuthorProfileCache;
import com.github.spjavaind300.commentsservice.cache.ProfileCacheService;
import com.github.spjavaind300.commentsservice.dto.CommentTextDto;
import com.github.spjavaind300.commentsservice.dto.ProfileDto;
import com.github.spjavaind300.commentsservice.dto.Role;
import com.github.spjavaind300.commentsservice.dto.UserContext;
import com.github.spjavaind300.commentsservice.exception.ForbiddenException;
import com.github.spjavaind300.commentsservice.exception.UnauthorizedException;
import com.github.spjavaind300.commentsservice.feing.AdsFeignClientInternal;
import com.github.spjavaind300.commentsservice.feing.ProfileFeignClientInternal;
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
import java.util.Set;

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
    private ProfileCacheService profileCacheService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private ProfileFeignClientInternal profileFeignClient;

    @MockitoBean
    private AdsFeignClientInternal adsFeignClientInternal;

    @MockitoBean
    private AuthorProfileCache authorProfileCache;

    private CommentServiceImpl commentService;

    private ProfileDto testProfile;

    @BeforeEach
    void setUp() {
        commentService = new CommentServiceImpl(
                commentRepository,
                commentMapper,
                adsFeignClientInternal,
                jwtUtils,
                profileCacheService
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
    @DisplayName("Получение комментариев для объявления — комментарии найдены")
    void test_getCommentsForAd_returnsList() {
        Comment comment = commentRepository.save(Comment.builder()
                .adId(1)
                .authorId(testProfile.getAuthorId())
                .text("Комментарий")
                .createdAt(Instant.now())
                .build());

        when(profileCacheService.getProfile(testProfile.getAuthorId())).thenReturn(testProfile);

        var result = commentService.getCommentsForAd(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Комментарий", result.getFirst().getText());
    }

    @Test
    @DisplayName("Получение комментариев для объявления — нет комментариев")
    void test_getCommentsForAd_noComments_returnsEmptyList() {
        when(profileCacheService.getProfile(testProfile.getAuthorId())).thenReturn(testProfile);

        var result = commentService.getCommentsForAd(1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
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

        when(profileCacheService.getProfile(testProfile.getAuthorId())).thenReturn(testProfile);
        when(jwtUtils.getUserContext()).thenReturn(new UserContext(testProfile.getAuthorId(), Role.USER));

        var result = commentService.addComment(1, commentTextDto);

        assertNotNull(result);
        assertEquals("Комментарий", result.getText());
    }

    @Test
    @DisplayName("Добавление комментария — профиль из feign")
    void test_addComment_profileFetchedViaFeign() {
        ProfileDto testProfile = new ProfileDto();
        testProfile.setAuthorId(1L);
        testProfile.setAuthorFirstName("Иван");
        testProfile.setAuthorImage("avatar.jpg");

        CommentTextDto commentTextDto = new CommentTextDto();
        commentTextDto.setText("Новый комментарий");

        commentRepository.save(Comment.builder()
                .adId(1)
                .authorId(testProfile.getAuthorId())
                .text("Ранее добавленный")
                .createdAt(Instant.now())
                .build());

        when(profileCacheService.getProfile(testProfile.getAuthorId())).thenReturn(null);
        when(profileFeignClient.getProfileByIdInternal(testProfile.getAuthorId())).thenReturn(testProfile);
        when(jwtUtils.getUserContext()).thenReturn(new UserContext(testProfile.getAuthorId(), Role.USER));

        when(profileCacheService.getProfile(testProfile.getAuthorId())).thenReturn(testProfile);

        var result = commentService.addComment(1, commentTextDto);

        assertNotNull(result);
        assertEquals("Новый комментарий", result.getText());
    }

    @Test
    @DisplayName("Добавление комментария — юзер контекст отсутствует → UnauthorizedException")
    void test_addComment_noUserContext_throwsUnauthorized() {
        when(jwtUtils.getUserContext()).thenReturn(null);

        CommentTextDto commentTextDto = new CommentTextDto();
        commentTextDto.setText("Ошибка");

        assertThrows(UnauthorizedException.class, () -> commentService.addComment(1, commentTextDto));
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

        when(jwtUtils.getUserContext()).thenReturn(new UserContext(testProfile.getAuthorId(), Role.USER));
        when(profileCacheService.getProfile(testProfile.getAuthorId())).thenReturn(testProfile);

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

        when(jwtUtils.getUserContext()).thenReturn(new UserContext(2L, Role.USER));
        when(profileCacheService.getProfile(2L)).thenReturn(other);

        assertThrows(ForbiddenException.class, () -> commentService.updateComment(1, saved.getId(), commentTextDto));
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

        when(jwtUtils.getUserContext()).thenReturn(new UserContext(testProfile.getAuthorId(), Role.USER));
        when(profileCacheService.getProfile(testProfile.getAuthorId())).thenReturn(testProfile);

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

        when(jwtUtils.getUserContext()).thenReturn(new UserContext(2L, Role.USER));
        when(profileCacheService.getProfile(2L)).thenReturn(other);

        assertThrows(ForbiddenException.class, () -> commentService.deleteComment(1, saved.getId()));
    }

    @Test
    @DisplayName("Удаление комментария — комментарий не найден, ничего не делаем")
    void test_deleteComment_commentNotFound() {
        when(jwtUtils.getUserContext()).thenReturn(new UserContext(testProfile.getAuthorId(), Role.USER));
        when(profileCacheService.getProfile(testProfile.getAuthorId())).thenReturn(testProfile);

        commentService.deleteComment(1, 999);

        assertTrue(commentRepository.findAll().isEmpty());
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

        when(jwtUtils.getUserContext())
                .thenReturn(new UserContext(testProfile.getAuthorId(), Role.USER));

        when(authorProfileCache.get(testProfile.getAuthorId())).thenReturn(null);

        when(profileFeignClient.getProfileByIdInternal(testProfile.getAuthorId()))
                .thenReturn(testProfile);

        when(profileCacheService.getProfile(testProfile.getAuthorId()))
                .thenAnswer(invocation -> {
                    authorProfileCache.put(testProfile.getAuthorId(), testProfile);
                    return testProfile;
                });

        commentService.addComment(1, commentTextDto);

        verify(authorProfileCache, times(1)).put(testProfile.getAuthorId(), testProfile);
    }

    @Test
    @DisplayName("Получение авторов комментариев для объявления — комментарии найдены")
    void test_getAuthorIdsByAdId_returnsAuthorsSet() {
        Comment c1 = commentRepository.save(Comment.builder()
                .adId(1)
                .authorId(100L)
                .text("Комментарий 1")
                .createdAt(Instant.now())
                .build());

        Comment c2 = commentRepository.save(Comment.builder()
                .adId(1)
                .authorId(101L)
                .text("Комментарий 2")
                .createdAt(Instant.now())
                .build());

        Comment c3 = commentRepository.save(Comment.builder()
                .adId(1)
                .authorId(100L)
                .text("Комментарий 3")
                .createdAt(Instant.now())
                .build());

        Set<Long> result = commentService.getAuthorIdsByAdId(1);

        assertNotNull(result);
        assertEquals(Set.of(100L, 101L), result);
    }

    @Test
    @DisplayName("Получение авторов комментариев для объявления — комментарии отсутствуют")
    void test_getAuthorIdsByAdId_returnsEmptySet() {
        Set<Long> result = commentService.getAuthorIdsByAdId(999);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
package com.github.spjavaind300.notificationservice.service.strategy;

import com.github.spjavaind300.notificationservice.model.NotificationType;
import com.github.spjavaind300.notificationservice.model.dto.AdvUpdatedEvent;
import com.github.spjavaind300.notificationservice.model.dto.EmailDto;
import com.github.spjavaind300.notificationservice.model.dto.UserDto;
import com.github.spjavaind300.notificationservice.service.CommentService;
import com.github.spjavaind300.notificationservice.service.ProfileService;
import com.github.spjavaind300.notificationservice.utils.EmailTemplateCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdvUpdatedStrategyTest {

    private static final String CAPTION = "test caption";
    private static final String SUPPORT_LINK = "test link";
    private static final String TEMPLATE = """
            template:
            Subject: %s
            Name: %s
            Title: %s
            Title: %s
            URL: %s
            Caption: %s
            Support: %s
            """;

    @Mock
    private EmailTemplateCache templateCache;

    @Mock
    private CommentService commentService;

    @Mock
    private ProfileService profileService;

    private AdvUpdatedStrategy strategy;


    @BeforeEach
    void setUp() {
        strategy = new AdvUpdatedStrategy(CAPTION, SUPPORT_LINK, templateCache, commentService, profileService);
    }

    @Test
    void test_getType() {
        assertEquals(NotificationType.ADV_UPDATED, strategy.getType());
    }

    @Test
    void test_prepareEmail() {

        AdvUpdatedEvent event = new AdvUpdatedEvent(1, "title","firstName", "lastName", "advURI");
        List<Integer> ids = List.of(1,2,5);
        UserDto user1 = new UserDto(1,"Author1 FirstName", "Author1 LastName", "author1@test.com");
        UserDto user2 = new UserDto(2,"Author2 FirstName", "Author2 LastName", "author2@test.com");
        UserDto user5 = new UserDto(5,"Author5 FirstName", "Author5 LastName", "author5@test.com");
        List<UserDto> users = List.of(user1,user2,user5);

        when(templateCache.getTemplate(NotificationType.ADV_UPDATED)).thenReturn(TEMPLATE);
        when(commentService.getCommentsAuthorIdsOfAdv(event.id())).thenReturn(ids);
        when(profileService.getProfiles(ids)).thenReturn(users);

        List<EmailDto> emails = strategy.prepareEmail(event);

        assertNotNull(emails);
        assertEquals(users.size(), emails.size());
        List<String> actualEmails = emails.stream().map(EmailDto::getEmail).toList();
        assertTrue(actualEmails.containsAll(users.stream().map(UserDto::getEmail).toList()));
    }
}
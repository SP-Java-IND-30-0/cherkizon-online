package com.github.spjavaind300.notificationservice.service.strategy;

import com.github.spjavaind300.notificationservice.model.NotificationType;
import com.github.spjavaind300.notificationservice.model.dto.*;
import com.github.spjavaind300.notificationservice.service.AdvService;
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
class CommentCreatedStrategyTest {

    private static final String CAPTION = "test caption";
    private static final String SUPPORT_LINK = "test link";
    private static final String TEMPLATE = """
            template:
            Subject: %s
            Name: %s
            Author: %s
            Title: %s
            URL: %s
            Caption: %s
            Support: %s
            """;

    @Mock
    private EmailTemplateCache templateCache;

    @Mock
    private AdvService advService;

    @Mock
    private ProfileService profileService;

    private CommentCreatedStrategy commentCreatedStrategy;

    @BeforeEach
    void setUp() {
        commentCreatedStrategy = new CommentCreatedStrategy(CAPTION, SUPPORT_LINK, templateCache, advService, profileService);
    }

    @Test
    void test_getType() {
        NotificationType actual = commentCreatedStrategy.getType();

        assertEquals(NotificationType.COMMENT_CREATED, actual);
    }

    @Test
    void test_prepareEmail() {

        CommentCreatedEvent event =
                new CommentCreatedEvent(1, 1, "firstName", "lastName", "commentURI");
        AdvDto adv = new AdvDto(event.advId(),"test title");
        UserDto user = new UserDto(1,"Author FirstName", "Author LastName", "author@test.com");

        when(templateCache.getTemplate(NotificationType.COMMENT_CREATED)).thenReturn(TEMPLATE);
        when(advService.getAdv(event.advId())).thenReturn(adv);
        when(profileService.getProfiles(List.of(adv.getId()))).thenReturn(List.of(user));

        List<EmailDto> emails = commentCreatedStrategy.prepareEmail(event);

        assertNotNull(emails);
        assertEquals(1, emails.size());
        assertEquals(user.getEmail(), emails.getFirst().getEmail());

    }
}
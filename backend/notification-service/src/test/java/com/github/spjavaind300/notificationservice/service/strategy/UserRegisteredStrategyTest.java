package com.github.spjavaind300.notificationservice.service.strategy;

import com.github.spjavaind300.notificationservice.model.NotificationType;
import com.github.spjavaind300.notificationservice.model.dto.EmailDto;
import com.github.spjavaind300.notificationservice.model.dto.UserRegisteredEvent;
import com.github.spjavaind300.notificationservice.utils.EmailTemplateCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRegisteredStrategyTest {

    private static final String CAPTION = "test caption";
    private static final String SUPPORT_LINK = "test link";
    private static final String TEMPLATE = """
            template:
            Subject: %s
            Name: %s
            Support: %s
            Caption: %s
            """;

    @Mock
    private EmailTemplateCache templateCache;

    private UserRegisteredStrategy userRegisteredStrategy;

    @BeforeEach
    void setUp() {
        userRegisteredStrategy = new UserRegisteredStrategy(CAPTION, SUPPORT_LINK, templateCache);
    }

    @Test
    void test_getType() {
        NotificationType actual = userRegisteredStrategy.getType();

        assertEquals(NotificationType.USER_CREATED, actual);
    }

    @Test
    void test_prepareEmail() {

        UserRegisteredEvent event = new UserRegisteredEvent(1, "test@test.com", "firstName", "lastName", "89999999999");
        when(templateCache.getTemplate(NotificationType.USER_CREATED)).thenReturn(TEMPLATE);

        List<EmailDto> emails = userRegisteredStrategy.prepareEmail(event);

        assertNotNull(emails);
        assertEquals(1, emails.size());
        assertEquals(event.email(), emails.getFirst().getEmail());
    }
}
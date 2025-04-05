package com.github.spjavaind300.notificationservice.service;

import com.github.spjavaind300.notificationservice.model.NotificationType;
import com.github.spjavaind300.notificationservice.model.dto.Event;
import com.github.spjavaind300.notificationservice.service.strategy.AdvUpdatedStrategy;
import com.github.spjavaind300.notificationservice.service.strategy.CommentCreatedStrategy;
import com.github.spjavaind300.notificationservice.service.strategy.UserRegisteredStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailStrategyFactoryTest {

    private EmailStrategyFactory emailStrategyFactory;
    private static final NotificationStrategy<? extends Event> advUpdatedStrategy = mock(AdvUpdatedStrategy.class);
    private static final NotificationStrategy<? extends Event> commentCreatedStrategy = mock(CommentCreatedStrategy.class);
    private static final NotificationStrategy<? extends Event> userRegisteredStrategy = mock(UserRegisteredStrategy.class);

    @BeforeEach
    void setUp() {

        when(advUpdatedStrategy.getType()).thenReturn(NotificationType.ADV_UPDATED);
        when(commentCreatedStrategy.getType()).thenReturn(NotificationType.COMMENT_CREATED);
        when(userRegisteredStrategy.getType()).thenReturn(NotificationType.USER_CREATED);

        @SuppressWarnings("unchecked")
        List<NotificationStrategy<Event>> strategies = (List<NotificationStrategy<Event>>) (List<?>) List.of(
                advUpdatedStrategy,
                commentCreatedStrategy,
                userRegisteredStrategy
        );

        emailStrategyFactory = new EmailStrategyFactory(strategies);
    }


    @ParameterizedTest
    @MethodSource("testDataProvider")
    void test_getStrategy(NotificationType type, NotificationStrategy<Event> expectedResult) {

        NotificationStrategy<Event> result = emailStrategyFactory.getStrategy(type);

        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    @SuppressWarnings("unchecked")
    private static Stream<Arguments> testDataProvider() {
        return Stream.of(
                Arguments.of(NotificationType.ADV_UPDATED, (NotificationStrategy<Event>) advUpdatedStrategy),
                Arguments.of(NotificationType.COMMENT_CREATED, (NotificationStrategy<Event>) commentCreatedStrategy),
                Arguments.of(NotificationType.USER_CREATED, (NotificationStrategy<Event>) userRegisteredStrategy)
        );

    }
}
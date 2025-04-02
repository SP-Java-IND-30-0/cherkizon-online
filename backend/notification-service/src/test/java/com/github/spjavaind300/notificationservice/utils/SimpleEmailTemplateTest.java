package com.github.spjavaind300.notificationservice.utils;

import com.github.spjavaind300.notificationservice.model.NotificationType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class SimpleEmailTemplateTest {

    private final SimpleEmailTemplate simpleEmailTemplate = new SimpleEmailTemplate();

    static Stream<Arguments> testDataProvider() {
        return Stream.of(
                Arguments.of(NotificationType.USER_CREATED, "Добро пожаловать!"),
                Arguments.of(NotificationType.ADV_UPDATED, "Кое-что изменилось!"),
                Arguments.of(NotificationType.COMMENT_CREATED, "Кто-то оставил вам комментарий!")
        );
    }

    @ParameterizedTest
    @MethodSource("testDataProvider")
    void getTemplate(NotificationType type, String expectedResult) {

        String result = simpleEmailTemplate.getTemplate(type);

        assertNotNull(result);
        assertTrue(result.contains(expectedResult));

    }
}
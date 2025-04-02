package com.github.spjavaind300.notificationservice.utils;

import com.github.spjavaind300.notificationservice.model.NotificationType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EmailTemplateCacheTest {

    @TempDir
    static Path tempDir;
    static Path templatesDir;
    static SimpleEmailTemplate mockSimpleTemplates;

    @BeforeAll
    static void setup() throws IOException {
        templatesDir = tempDir.resolve("email-templates");
        Files.createDirectory(templatesDir);


        for (NotificationType type : NotificationType.values()) {
            String filename = type.getTemplateName();
            Files.writeString(
                    templatesDir.resolve(filename),
                    "<html>Template for " + type + "</html>"
            );
        }

        mockSimpleTemplates = mock(SimpleEmailTemplate.class);
        when(mockSimpleTemplates.getTemplate(any()))
                .thenReturn("<html>Simple template</html>");
    }

    @Test
    void test_getTemplate_shouldLoadTemplateFromFile() {
        EmailTemplateCache cache = new EmailTemplateCache(
                mockSimpleTemplates,
                "file:" + templatesDir.toString()
        );

        String content = cache.getTemplate(NotificationType.USER_CREATED);
        assertEquals("<html>Template for USER_CREATED</html>", content);
    }

    @Test
    void test_getTemplate_shouldUseSimpleTemplateWhenFileMissing() {
        EmailTemplateCache cache = new EmailTemplateCache(
                mockSimpleTemplates,
                "file:/non/existing/path"
        );

        String content = cache.getTemplate(NotificationType.USER_CREATED);
        assertEquals("<html>Simple template</html>", content);
    }

    @Test
    void test_getTemplate_shouldInvalidateCacheOnFileChange() throws IOException, InterruptedException {
        EmailTemplateCache cache = new EmailTemplateCache(
                mockSimpleTemplates,
                "file:" + templatesDir.toString()
        );


        String original = cache.getTemplate(NotificationType.USER_CREATED);
        assertEquals("<html>Template for USER_CREATED</html>", original);

        Files.writeString(
                templatesDir.resolve(NotificationType.USER_CREATED.getTemplateName()),
                "<html>Modified template</html>"
        );

        Thread.sleep(1000);

        // Проверяем что кеш обновился
        String updated = cache.getTemplate(NotificationType.USER_CREATED);
        assertEquals("<html>Modified template</html>", updated);
    }
}
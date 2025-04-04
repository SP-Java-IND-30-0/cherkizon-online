package com.github.spjavaind300.notificationservice.utils;

import com.github.spjavaind300.notificationservice.model.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

@Component
@Slf4j
public class EmailTemplateCache {

    private final SimpleEmailTemplate simpleTemplates;
    private final Map<NotificationType, String> templates = new ConcurrentHashMap<>();
    private final Path emailTemplatesDir;

    public EmailTemplateCache(SimpleEmailTemplate simpleTemplates, @Value("${email.templates.dir}") String templatesDir) {
        this.simpleTemplates = simpleTemplates;
        this.emailTemplatesDir = Paths.get(templatesDir.startsWith("file:")
                ? templatesDir.substring(5)  // Удаляем префикс "file:"
                : templatesDir);
        if (!Files.exists(emailTemplatesDir)) {
            log.warn("Templates dir not found: {}", templatesDir);
            for (NotificationType type : NotificationType.values()) {
                templates.put(type, simpleTemplates.getTemplate(type));
            }
        } else {
            startTemplateWatcher();
        }
    }

    public String getTemplate(NotificationType name) {
        return templates.computeIfAbsent(name, this::loadTemplate);
    }

    private String loadTemplate(NotificationType name) {
        try {
            Path file = emailTemplatesDir.resolve(name.getTemplateName());
            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("Failed to read template: {}", name.getTemplateName(), e);
            return simpleTemplates.getTemplate(name);
        }
    }

    private void startTemplateWatcher() {
        try {
            WatchService watchService = emailTemplatesDir.getFileSystem().newWatchService();
            emailTemplatesDir.register(watchService, ENTRY_MODIFY);

            new Thread(() -> {
                while (true) {
                    WatchKey key;
                    try {
                        key = watchService.take();
                        for (WatchEvent<?> event : key.pollEvents()) {
                            if (event.kind() == ENTRY_MODIFY) {
                                invalidateCache(event.context().toString());
                            }
                        }
                        key.reset();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }).start();
        } catch (IOException e) {
            log.error("Failed to start email templates watcher", e);
        }
    }

    private void invalidateCache(String fileName) {
        for (NotificationType name : NotificationType.values()) {
            if (name.getTemplateName().equals(fileName)) {
                templates.remove(name);
                log.info("Template invalidated: {}", fileName);
                break;
            }
        }
    }

}

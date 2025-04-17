package com.github.spjavaind300.commentsservice.controller;

import com.github.spjavaind300.commentsservice.dto.CommentDto;
import com.github.spjavaind300.commentsservice.dto.CommentTextDto;
import com.github.spjavaind300.commentsservice.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Получение комментариев объявления")
    @ApiResponse(responseCode = "200", description = "Комментарии успешно получены")
    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    @GetMapping("/{adId}/comments")
    public ResponseEntity<Map<String, List<CommentDto>>> getComments(@PathVariable int adId) {
        List<CommentDto> comments = commentService.getCommentsForAd(adId);
        return ResponseEntity.ok(Map.of("results", comments));
    }

    @Operation(summary = "Добавление комментария к объявлению")
    @ApiResponse(responseCode = "200", description = "Комментарий успешно добавлен")
    @ApiResponse(responseCode = "400", description = "Некорректные данные в теле запроса")
    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    @PostMapping("/{adId}/comments")
    public ResponseEntity<CommentDto> addComment(@PathVariable int adId,
                                                 @RequestBody @Valid CommentTextDto commentTextDto) {
        CommentDto comment = commentService.addComment(adId, commentTextDto);
        return ResponseEntity.ok(comment);
    }

    @Operation(summary = "Обновление комментария")
    @ApiResponse(responseCode = "200", description = "Комментарий успешно обновлён")
    @ApiResponse(responseCode = "400", description = "Некорректные данные в теле запроса")
    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    @ApiResponse(responseCode = "404", description = "Комментарий или объявление не найдено")
    @PatchMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable int adId,
                                                    @PathVariable int commentId,
                                                    @RequestBody @Valid CommentTextDto commentTextDto) {
        CommentDto updated = commentService.updateComment(adId, commentId, commentTextDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Удаление комментария")
    @ApiResponse(responseCode = "204", description = "Комментарий успешно удалён")
    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    @ApiResponse(responseCode = "404", description = "Комментарий или объявление не найдено")
    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable int adId,
                                              @PathVariable int commentId) {
        commentService.deleteComment(adId, commentId);
        return ResponseEntity.noContent().build();
    }
}
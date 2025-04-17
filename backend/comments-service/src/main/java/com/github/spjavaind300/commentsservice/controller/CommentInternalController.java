package com.github.spjavaind300.commentsservice.controller;

import com.github.spjavaind300.commentsservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/internal/comment")
@RequiredArgsConstructor
public class CommentInternalController {

    private final CommentService commentService;

    @GetMapping("/{adId}/authors")
    public ResponseEntity<Set<Long>> getCommentAuthors(@PathVariable int adId) {
        Set<Long> authorIds = commentService.getAuthorIdsByAdId(adId);
        return ResponseEntity.ok(authorIds);
    }
}
package com.github.spjavaind300.commentsservice.service;

import com.github.spjavaind300.commentsservice.dto.CommentDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> getCommentsForAd(int adId);

    CommentDto addComment(int adId, String text);

    CommentDto updateComment(int adId, int commentId, String newText);

    void deleteComment(int adId, int commentId);
}
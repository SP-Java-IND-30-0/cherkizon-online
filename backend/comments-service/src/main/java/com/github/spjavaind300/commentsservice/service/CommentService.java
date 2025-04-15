package com.github.spjavaind300.commentsservice.service;

import com.github.spjavaind300.commentsservice.dto.CommentDto;
import com.github.spjavaind300.commentsservice.dto.CommentTextDto;

import java.util.List;
import java.util.Map;

public interface CommentService {

    Map<String, List<CommentDto>> getCommentsForAd(int adId, int page, int size);

    CommentDto addComment(int adId, CommentTextDto commentTextDto);

    CommentDto updateComment(int adId, int commentId, CommentTextDto commentTextDto);

    void deleteComment(int adId, int commentId);
}
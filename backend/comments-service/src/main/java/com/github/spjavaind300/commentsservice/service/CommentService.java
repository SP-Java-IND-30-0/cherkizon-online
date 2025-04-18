package com.github.spjavaind300.commentsservice.service;

import com.github.spjavaind300.commentsservice.dto.CommentDto;
import com.github.spjavaind300.commentsservice.dto.CommentTextDto;

import java.util.List;
import java.util.Set;

public interface CommentService {

    List<CommentDto> getCommentsForAd(int adId);

    CommentDto addComment(int adId, CommentTextDto commentTextDto);

    CommentDto updateComment(int adId, int commentId, CommentTextDto commentTextDto);

    void deleteComment(int adId, int commentId);

    Set<Long> getAuthorIdsByAdId(int adId);

    void deleteCommentsByAuthorId(long authorId);

    void deleteCommentsByAdId(int adId);
}
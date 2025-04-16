package com.github.spjavaind300.commentsservice.exception;

import com.github.spjavaind300.commentsservice.dto.Role;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(long requesterId, Role role, long commentAuthorId, int commentId) {
        super(String.format(
                "Доступ запрещён: пользователь с id=%d (роль: %s) пытался изменить комментарий id=%d, автором которого является id=%d",
                requesterId, role, commentId, commentAuthorId
        ));
    }
}
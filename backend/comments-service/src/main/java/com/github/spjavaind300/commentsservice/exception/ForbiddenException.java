package com.github.spjavaind300.commentsservice.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(int commentId) {
        super("Нет прав на редактирование комментария с id=" + commentId);
    }
}
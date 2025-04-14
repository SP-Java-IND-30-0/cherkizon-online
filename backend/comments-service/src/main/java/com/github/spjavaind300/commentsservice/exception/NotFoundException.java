package com.github.spjavaind300.commentsservice.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(int commentId) {
        super("Комментарий с id=" + commentId + " не найден");
    }
}
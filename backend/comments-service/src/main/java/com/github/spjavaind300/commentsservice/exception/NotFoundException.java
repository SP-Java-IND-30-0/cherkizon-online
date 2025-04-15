package com.github.spjavaind300.commentsservice.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String entity, Object id) {
        super(entity + " с id=" + id + " не найден");
    }
}
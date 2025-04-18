package com.github.spjavaind300.commentsservice.exception;

public class InvalidJwtException extends RuntimeException {

    public InvalidJwtException(Throwable cause) {
        super("Ошибка с JWT токеном: " + cause.getMessage(), cause);
    }
}
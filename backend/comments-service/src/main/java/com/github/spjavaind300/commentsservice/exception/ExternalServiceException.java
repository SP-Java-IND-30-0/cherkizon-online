package com.github.spjavaind300.commentsservice.exception;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message, Throwable cause) {
        super("Ошибка при обращении к внешнему сервису: " + message, cause);
    }
}
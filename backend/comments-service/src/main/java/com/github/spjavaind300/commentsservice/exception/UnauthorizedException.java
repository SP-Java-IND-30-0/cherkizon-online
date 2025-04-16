package com.github.spjavaind300.commentsservice.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Ошибка авторизации: не удалось найти пользователя в контексте");
    }
}
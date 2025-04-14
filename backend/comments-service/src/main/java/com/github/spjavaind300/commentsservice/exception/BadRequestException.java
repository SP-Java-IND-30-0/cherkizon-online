package com.github.spjavaind300.commentsservice.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(int commentId) {
        super("Комментарий с id=" + commentId + " не принадлежит данному объявлению");
    }

    public BadRequestException() {
        super("Некорректный запрос: не удалось получить текущий профиль");
    }
}
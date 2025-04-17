package com.github.spjavaind300.commentsservice.exception;

public class AdNotFoundException extends RuntimeException {

    public AdNotFoundException(int adId) {
        super("Объявление с id " + adId + " не найдено");
    }
}
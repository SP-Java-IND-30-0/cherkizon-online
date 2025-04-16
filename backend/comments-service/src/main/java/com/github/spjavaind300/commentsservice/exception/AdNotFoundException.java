package com.github.spjavaind300.commentsservice.exception;

import lombok.Getter;

@Getter
public class AdNotFoundException extends RuntimeException {
    private final int adId;

    public AdNotFoundException(int adId) {
        super("Объявление с id " + adId + " не найдено");
        this.adId = adId;
    }
}
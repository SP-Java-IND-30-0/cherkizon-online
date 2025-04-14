package com.github.spjavaind300.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(int id) {
        super("Advertisement with id=" + id + "not found");
    }
}

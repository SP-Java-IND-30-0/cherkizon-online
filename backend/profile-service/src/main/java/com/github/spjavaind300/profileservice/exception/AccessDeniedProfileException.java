package com.github.spjavaind300.profileservice.exception;

public class AccessDeniedProfileException extends RuntimeException {
    public AccessDeniedProfileException() {
        super("Access denied");
    }
}


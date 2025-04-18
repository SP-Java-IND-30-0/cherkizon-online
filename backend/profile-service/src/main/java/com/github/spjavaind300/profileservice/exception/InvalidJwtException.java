package com.github.spjavaind300.profileservice.exception;

public class InvalidJwtException extends RuntimeException {

    public InvalidJwtException(Throwable cause) {
        super(cause.getMessage());
    }
}

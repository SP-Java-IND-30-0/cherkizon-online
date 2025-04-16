package com.github.spjavaind300.exception;

public class InvalidJwtException extends RuntimeException {

    public InvalidJwtException(Throwable cause) {
        super(cause.getMessage());
    }
}

package com.github.spjavaind300.exception;

public class ProfileRequestFailedException extends RuntimeException {
    public ProfileRequestFailedException(long id, Throwable cause) {

        super("Failed to get user with id=" + id, cause);
    }
}

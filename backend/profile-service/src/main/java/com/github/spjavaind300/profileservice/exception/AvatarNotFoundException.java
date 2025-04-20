package com.github.spjavaind300.profileservice.exception;

public class AvatarNotFoundException extends RuntimeException {
    public AvatarNotFoundException(String key, Throwable cause) {
        super("Avatar not found: " + key, cause);
    }
    public AvatarNotFoundException(String message) {
        super(message);
    }
}

package com.github.spjavaind300.profileservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class AvatarStorageException extends RuntimeException {
    public AvatarStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

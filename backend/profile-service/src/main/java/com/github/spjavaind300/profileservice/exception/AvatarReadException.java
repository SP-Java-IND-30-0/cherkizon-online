package com.github.spjavaind300.profileservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AvatarReadException extends RuntimeException {
    public AvatarReadException(String message, Throwable cause) {
        super(message, cause);
    }
}


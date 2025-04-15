package com.github.spjavaind300.profileservice.exception;

import org.springframework.security.core.AuthenticationException;

public class UserNotFoundAuthException extends AuthenticationException {
    public UserNotFoundAuthException(String msg) {
        super(msg);
    }
}


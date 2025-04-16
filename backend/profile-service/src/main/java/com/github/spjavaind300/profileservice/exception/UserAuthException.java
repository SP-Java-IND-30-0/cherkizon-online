package com.github.spjavaind300.profileservice.exception;

import org.springframework.security.core.AuthenticationException;

public class UserAuthException extends AuthenticationException {
    public UserAuthException(String msg) {
        super(msg);
    }
}

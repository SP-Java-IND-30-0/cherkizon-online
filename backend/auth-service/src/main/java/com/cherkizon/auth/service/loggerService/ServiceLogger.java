package com.cherkizon.auth.service.loggerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServiceLogger {
    private ServiceLogger() {}

    public static final Logger AUTH =
            LoggerFactory.getLogger("com.cherkizon.auth.service.AuthService");

    public static final Logger JWT =
            LoggerFactory.getLogger("com.cherkizon.auth.service.JwtService");
}
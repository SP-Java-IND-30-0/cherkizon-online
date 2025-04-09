package com.github.spjavaind300.profileservice.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordPatternValidator.class)
public @interface ValidPassword {
    String message() default "Новый пароль должен содержать " +
            "хотя бы одну заглавную букву, одну цифру и быть от 8 до 16 символов.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


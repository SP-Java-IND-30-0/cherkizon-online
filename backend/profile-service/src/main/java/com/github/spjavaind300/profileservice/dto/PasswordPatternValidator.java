package com.github.spjavaind300.profileservice.dto;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

public class PasswordPatternValidator implements ConstraintValidator<ValidPassword, String> {

    @Value("${validation.patterns.password}")
    private String passwordPattern;

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }
        return password.matches(passwordPattern);
    }

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }
}


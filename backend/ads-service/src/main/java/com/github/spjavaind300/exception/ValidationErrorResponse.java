package com.github.spjavaind300.exception;

import java.util.List;

public record ValidationErrorResponse(List<Violation> violations) {

    public record Violation(String fieldName, String message) {}
}

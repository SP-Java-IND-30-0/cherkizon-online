package com.github.spjavaind300.advice;

import com.fasterxml.jackson.core.JacksonException;
import com.github.spjavaind300.exception.AccessDeniedException;
import com.github.spjavaind300.exception.NotFoundException;
import com.github.spjavaind300.exception.ProfileRequestFailedException;
import com.github.spjavaind300.exception.ValidationErrorResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Log4j2
public class GlobalControllerAdvice {


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HandlerMethodValidationException.class})
    public ResponseEntity<ValidationErrorResponse> handleValidationException(Exception e) {
        List<ValidationErrorResponse.Violation> violations = new ArrayList<>();

        if (e instanceof MethodArgumentNotValidException ex) {
            violations = ex.getBindingResult().getFieldErrors().stream()
                    .map(error -> new ValidationErrorResponse.Violation(error.getField(), error.getDefaultMessage()))
                    .toList();
        } else if (e instanceof HandlerMethodValidationException ex) {
            violations = ex.getParameterValidationResults().stream()
                    .flatMap(result -> result.getResolvableErrors().stream()
                            .map(error -> new ValidationErrorResponse.Violation(
                                    result.getMethodParameter().getParameterName(),
                                    error.getDefaultMessage())))
                    .toList();
        }

        return ResponseEntity.badRequest().body(new ValidationErrorResponse(violations));
    }

    @ExceptionHandler({
            ProfileRequestFailedException.class,
            S3Exception.class
    })
    public ResponseEntity<String> handleProfileRequestFailedException(ProfileRequestFailedException e) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(e.getMessage());
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            JacksonException.class
    })
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<String> handleSQLException(SQLException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }


}

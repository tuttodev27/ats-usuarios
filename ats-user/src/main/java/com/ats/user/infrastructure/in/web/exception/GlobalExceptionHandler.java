package com.ats.user.infrastructure.in.web.exception;

import com.ats.user.domain.exception.EmailAlreadyExistException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {EmailAlreadyExistException.class})
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(Instant.now(), 409, "EMAIL_ALREDY_EXIST", ex.getMessage(), request.getRequestURI())
        );
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err->err.getField() + ": " + err.getDefaultMessage())
                .orElse("Validation error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse(Instant.now(), 400, "VALIDATION_ERROR", msg, request.getRequestURI())
        );
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse(Instant.now(), 500, "INTERNAL_ERROR", "Unexpected error", request.getRequestURI())
        );
    }

}


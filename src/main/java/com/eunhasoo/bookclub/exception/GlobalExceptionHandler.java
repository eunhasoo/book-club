package com.eunhasoo.bookclub.exception;

import com.eunhasoo.bookclub.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException ex) {
        int statusCode = HttpStatus.BAD_REQUEST.value();

        String validMessage = ex.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        ErrorResponse body = ErrorResponse.builder()
                .message(validMessage)
                .statusCode(String.valueOf(statusCode))
                .build();

        return ResponseEntity
                .status(HttpStatus.valueOf(statusCode))
                .body(body);
    }

    @ExceptionHandler(BookClubException.class)
    public ResponseEntity<ErrorResponse> handleApplicationError(BookClubException ex) {
        int statusCode = ex.getStatusCode();

        ErrorResponse body = ErrorResponse.builder()
                .message(ex.getMessage())
                .statusCode(String.valueOf(statusCode))
                .build();

        return ResponseEntity
                .status(HttpStatus.valueOf(statusCode))
                .body(body);
    }
}

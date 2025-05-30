package com.wegagenbank.TransactionService.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTRansactionNotFound(TransactionNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setError("Not Found");
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setPath("/api/v1/transaction"); // Update this if needed

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Optionally handle other exceptions here
}

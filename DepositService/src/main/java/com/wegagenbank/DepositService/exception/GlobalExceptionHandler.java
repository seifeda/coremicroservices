package com.wegagenbank.DepositService.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DepositNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDepositNotFound(DepositNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setError("Not Found");
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setPath("/api/v1/deposit"); // Update this if needed

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Optionally handle other exceptions here
}


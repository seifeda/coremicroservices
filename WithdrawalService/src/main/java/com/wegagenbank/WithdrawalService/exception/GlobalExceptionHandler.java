package com.wegagenbank.WithdrawalService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WithdrawalNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWithdrawalNotFound(WithdrawalNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value()); // Change to BAD_REQUEST
        errorResponse.setError("Insufficient Funds"); // More relevant error description
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setPath("/api/v1/withdrawal"); // Keep this if relevant to the specific path

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // Return BAD_REQUEST status
    }

    // Optionally handle other exceptions here
}

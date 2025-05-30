package com.wegagenbank.BalanceService.exception;

public class BalanceNotFoundException extends RuntimeException{
    public BalanceNotFoundException(String message) {
        super(message);
    }
}

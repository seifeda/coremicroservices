package com.wegagenbank.DepositService.exception;

public class DepositNotFoundException extends  RuntimeException{
    public DepositNotFoundException(String message) {
        super(message);
    }
}

package com.wegagenbank.DepositService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private String message;
    private boolean success;
}




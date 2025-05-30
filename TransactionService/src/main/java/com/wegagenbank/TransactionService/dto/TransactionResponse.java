package com.wegagenbank.TransactionService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private Long id;
    private String accountNumber;
    private String transactionType;
    private Double amount;

    private LocalDateTime transactionDate;

    private String transactionStatus;

    private String description;
}

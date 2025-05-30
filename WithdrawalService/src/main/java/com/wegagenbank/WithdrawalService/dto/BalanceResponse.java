package com.wegagenbank.WithdrawalService.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceResponse {
    //private Long id;

    private String accountNumber;


    private Double currentBalance;

    private LocalDateTime lastUpdated;

   private String transactionType="WITHDRAWAL";  // e.g., Deposit, Withdrawal, Transfer


}
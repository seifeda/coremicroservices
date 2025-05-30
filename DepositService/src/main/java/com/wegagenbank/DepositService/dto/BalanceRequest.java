package com.wegagenbank.DepositService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceRequest {
    // private Long id;

    private String accountNumber;


    private Double currentBalance;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy HH:mm:ss[.SSS][XXX]")
    private LocalDateTime lastUpdated;

    private String transactionType;  // e.g., Deposit, Withdrawal, Transfer

}
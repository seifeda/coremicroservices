package com.wegagenbank.WithdrawalService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepositResponse {
    private Long id;
    private String accountNumber;
    private Double amount;
    private LocalDateTime depositDate;
    private String status;
}

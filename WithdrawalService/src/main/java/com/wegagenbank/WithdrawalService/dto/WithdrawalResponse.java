package com.wegagenbank.WithdrawalService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WithdrawalResponse {
    private Long id;

    private String accountNumber;

    private Double amount;


    private LocalDateTime withdrawalDate;


    private String status;  // e.g., COMPLETED, FAILED


}

package com.wegagenbank.BalanceService.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceResponse {
    private Long id;

    private String accountNumber;


    private Double currentBalance;

    private LocalDateTime lastUpdated;
    private String transactionType;

}

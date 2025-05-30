package com.wegagenbank.DepositService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class DepositRequest {
    private String accountNumber;
    private Double amount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy HH:mm:ss[.SSS][XXX]")
    private LocalDateTime depositDate;
    private String status;  // e.g., COMPLETED, PENDING
    private String transactionType;
}

package com.wegagenbank.WithdrawalService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WithdrawalRequest {
    private Long id;
    private String accountNumber;
    private Double amount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy HH:mm:ss[.SSS][XXX]")
    private LocalDateTime withdrawalDate;
    private String status;  // e.g., COMPLETED, FAILED
}

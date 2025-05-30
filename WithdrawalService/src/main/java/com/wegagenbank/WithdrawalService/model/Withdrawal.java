package com.wegagenbank.WithdrawalService.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "withdrawals")
public class Withdrawal {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "withdrawal_seq_gen")
    @SequenceGenerator(name = "withdrawal_seq_gen", sequenceName = "withdrawal_seq", allocationSize = 1)
    private Long id;

    @NotNull
    @Size(max = 13)
    @Size(min=13)
    @Column(nullable = false)
    private String accountNumber;
    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDateTime withdrawalDate;

    @Column(nullable = false)
    private String status;  // e.g., COMPLETED, FAILED


}

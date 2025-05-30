package com.wegagenbank.TransactionService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq_gen")
    @SequenceGenerator(name = "transaction_seq_gen", sequenceName = "transaction_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String transactionType;  // e.g., Deposit, Withdrawal, Transfer

    @Column(nullable = false)
    private Double amount;

    @NotNull
    @Size(max = 13)
    @Size(min=13)
    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @Column(nullable = false)
    private String transactionStatus;  // e.g., SUCCESS, FAILED

    @Column(nullable = true)
    private String description;  // Additional transaction details

    // Getters and Setters
}
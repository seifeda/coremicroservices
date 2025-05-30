package com.wegagenbank.BalanceService.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "balances_new")
public class Balance {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "balance_seq_gen")
    @SequenceGenerator(name = "balance_seq_gen", sequenceName = "balance_seq", allocationSize = 1)
    private Long id;
    @NotNull
    @Size(min = 13, max = 13)
    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false)
    private Double currentBalance;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;
    @Column(nullable = false)
    private String transactionType;


}

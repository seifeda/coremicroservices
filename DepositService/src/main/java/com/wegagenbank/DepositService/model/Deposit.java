package com.wegagenbank.DepositService.model;

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
@Table(name = "deposits")
public class Deposit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "deposit_seq_gen")
    @SequenceGenerator(name = "deposit_seq_gen", sequenceName = "deposit_seq", allocationSize = 1)
    private Long id;

    @NotNull
    @Size(max = 13)
    @Size(min=13)
    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDateTime depositDate;

    @Column(nullable = false)
    private String status;  // e.g., COMPLETED, PENDING
}

package com.wegagenbank.CustomerService.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq_gen")
    @SequenceGenerator(name = "customer_seq_gen", sequenceName = "customer_seq", allocationSize = 1)
    private Long id;

    @NotNull
    @Size(max = 13)
    @Size(min=13)
    @Column(nullable = false, unique = true)
    private String accountNumber;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(nullable = false)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(nullable = false)
    private String lastName;

    @NotNull
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(nullable = false)
    private String phoneNumber;

    @NotNull
    @Column(nullable = false)
    private String address;

    @NotNull
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @NotNull
    @Column(nullable = false)
    private String accountType;

    @NotNull
    @Column(nullable = false)
    private String status;
}

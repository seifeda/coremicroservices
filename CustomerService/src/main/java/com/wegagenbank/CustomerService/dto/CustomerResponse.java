package com.wegagenbank.CustomerService.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse {
private Long id;
    private String accountNumber;
    private String firstName;
    private String lastName;

    private String email;

    private String phoneNumber;

    private String address;

    private LocalDate dateOfBirth;

    private String accountType;

    private String status;
}

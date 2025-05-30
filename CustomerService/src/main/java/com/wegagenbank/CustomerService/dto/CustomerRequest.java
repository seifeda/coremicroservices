package com.wegagenbank.CustomerService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequest {
    private String accountNumber;
    private String firstName;
    private String lastName;

    private String email;

    private String phoneNumber;

    private String address;
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private LocalDate dateOfBirth;

    private String accountType;

    private String status;
}

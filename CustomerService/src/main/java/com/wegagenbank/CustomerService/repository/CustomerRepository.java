package com.wegagenbank.CustomerService.repository;

import com.wegagenbank.CustomerService.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Optional<Customer> findByAccountNumber(String accountNumber);
}

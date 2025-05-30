package com.wegagenbank.TransactionService.repository;

import com.wegagenbank.TransactionService.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    Optional<Transaction> findByAccountNumber(String accountNumber);
}

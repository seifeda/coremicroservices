package com.wegagenbank.BalanceService.repository;

import com.wegagenbank.BalanceService.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
    Optional<Balance> findByAccountNumber(String accountNumber);
}

package com.wegagenbank.DepositService.repository;

import com.wegagenbank.DepositService.model.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepositRepository extends JpaRepository<Deposit, Long> {
    List<Deposit> findByAccountNumber(String accountNumber);
}

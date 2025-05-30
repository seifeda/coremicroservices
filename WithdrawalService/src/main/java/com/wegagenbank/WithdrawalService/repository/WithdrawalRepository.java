package com.wegagenbank.WithdrawalService.repository;

import com.wegagenbank.WithdrawalService.model.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {
    List<Withdrawal> findByAccountNumber(String accountNumber);
}

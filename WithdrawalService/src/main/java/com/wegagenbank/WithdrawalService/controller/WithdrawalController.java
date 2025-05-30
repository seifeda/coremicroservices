package com.wegagenbank.WithdrawalService.controller;

import com.wegagenbank.WithdrawalService.dto.WithdrawalRequest;
import com.wegagenbank.WithdrawalService.dto.WithdrawalResponse;
import com.wegagenbank.WithdrawalService.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/withdrawal")
@RequiredArgsConstructor
public class WithdrawalController {
    private final WithdrawalService withdrawalService;
    private static final Logger logger = LoggerFactory.getLogger(WithdrawalService.class);

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<WithdrawalResponse> getAllWithdrawals() {
        return withdrawalService.getAllWithdrawals();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void transactionWithdrawal(@RequestBody WithdrawalRequest withdrawalRequest) {
        withdrawalService.createWithdrawal(withdrawalRequest);
    }

    @GetMapping("/{accountNumber}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<WithdrawalResponse>> getWithdrawalsByAccountNumber(@PathVariable String accountNumber) {
        List<WithdrawalResponse> withdrawalResponses = withdrawalService.getWithdrawalsByAccountNumber(accountNumber);
        return new ResponseEntity<>(withdrawalResponses, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WithdrawalResponse updateWithdrawal(@PathVariable Long id, @RequestBody WithdrawalRequest withdrawalRequest) {
        return withdrawalService.updateWithdrawal(id, withdrawalRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWithdrawal(@PathVariable Long id) {
        withdrawalService.deleteWithdrawal(id);
    }
}

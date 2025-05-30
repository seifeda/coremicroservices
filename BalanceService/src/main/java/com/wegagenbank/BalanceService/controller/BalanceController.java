package com.wegagenbank.BalanceService.controller;

import com.wegagenbank.BalanceService.dto.BalanceRequest;
import com.wegagenbank.BalanceService.dto.BalanceResponse;
import com.wegagenbank.BalanceService.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;
    private static final Logger logger = LoggerFactory.getLogger(BalanceService.class);
    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> createOrUpdateBalance(@RequestBody BalanceRequest balanceRequest) {
        logger.info("Handling create or update balance request.");
        String responseMessage = balanceService.createOrUpdateBalance(balanceRequest);
       // return new ResponseEntity<>(responseMessage, HttpStatus.OK);

        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BalanceResponse> getAllBalance(){
        return balanceService.getAllBalance();
    }

    @GetMapping("/{accountNumber}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BalanceResponse> getBalanceByAccountNumber(@PathVariable String accountNumber) {
        BalanceResponse balanceResponse = balanceService.getBalanceByAccountNumber(accountNumber);
        return new ResponseEntity<>(balanceResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{accountNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBalance(@PathVariable String accountNumber) {
        balanceService.deleteBalance(accountNumber);
    }


}


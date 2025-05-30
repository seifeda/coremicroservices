package com.wegagenbank.TransactionService.controller;

import com.wegagenbank.TransactionService.dto.TransactionRequest;
import com.wegagenbank.TransactionService.dto.TransactionResponse;
import com.wegagenbank.TransactionService.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {
private final TransactionService transactionService;
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionResponse> getAllTransaction(){
        return transactionService.getAllTransaction();

    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> transactionCustomer(@RequestBody TransactionRequest transactionRequest){
        String responseMessage= transactionService.createTransaction(transactionRequest );

        return ResponseEntity.ok(responseMessage);
    }
    @GetMapping("/{accountNumber}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TransactionResponse> getTransactionByAccountNumber(@PathVariable String accountNumber) {
        TransactionResponse transactionResponse = transactionService.getTransactionByAccountNumber(accountNumber);
        return new ResponseEntity<>(transactionResponse, HttpStatus.OK);
    }

    @PutMapping("/{accountNumber}")
    @ResponseStatus(HttpStatus.OK)
    public TransactionResponse updateTransaction(@PathVariable String accountNumber, @RequestBody TransactionRequest transactionRequest) {
        return transactionService.updateTransaction(accountNumber, transactionRequest);
    }
    @DeleteMapping("/{accountNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable String accountNumber) {
        transactionService.deleteTransaction(accountNumber);
    }


}

package com.wegagenbank.DepositService.controller;

import com.wegagenbank.DepositService.dto.DepositRequest;
import com.wegagenbank.DepositService.dto.DepositResponse;
import com.wegagenbank.DepositService.service.DepositService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/deposit")
@RequiredArgsConstructor
public class DepositController {
    private final DepositService depositService;
    private static final Logger logger = LoggerFactory.getLogger(DepositService.class);
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<DepositResponse> getAllDeposit(){
        return depositService.getAllDeposit();

    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> transactionDeposit(@RequestBody DepositRequest depositRequest) {
        try {
            // Call the service layer to create the deposit
            String result = depositService.createDeposit(depositRequest);

            // Return the success message and status
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            // Handle invalid arguments or validation issues
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + e.getMessage());
        } catch (RuntimeException e) {
            // Handle general exceptions during deposit creation
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during deposit transaction: " + e.getMessage());
        }
    }


    @GetMapping("/{accountNumber}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<DepositResponse>> getDepositsByAccountNumber(@PathVariable String accountNumber) {
        List<DepositResponse> depositResponses = depositService.getDepositsByAccountNumber(accountNumber);
        return new ResponseEntity<>(depositResponses, HttpStatus.OK);
    }
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DepositResponse updateDeposit(@PathVariable Long id, @RequestBody DepositRequest depositRequest) {
        return depositService.updateDeposit(id, depositRequest);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDeposit(@PathVariable Long id) {
        depositService.deleteDeposit(id);
    }
}

package com.wegagenbank.BalanceService.service;

import com.wegagenbank.BalanceService.dto.BalanceRequest;
import com.wegagenbank.BalanceService.dto.BalanceResponse;
import com.wegagenbank.BalanceService.exception.BalanceNotFoundException;
import com.wegagenbank.BalanceService.model.Balance;
import com.wegagenbank.BalanceService.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {
    private final BalanceRepository balanceRepository;

    private static final Logger logger = LoggerFactory.getLogger(BalanceService.class);

    public List<BalanceResponse> getAllBalance() {
        List<Balance> balances = balanceRepository.findAll();
        return balances.stream().map(this::mapToBalanceResponse).toList();
    }

    private BalanceResponse mapToBalanceResponse(Balance balance) {
        return BalanceResponse.builder()
                .id(balance.getId())
                .accountNumber(balance.getAccountNumber())
                .currentBalance(balance.getCurrentBalance())
                .lastUpdated(balance.getLastUpdated())
                .build();
    }

    public String createBalance(BalanceRequest balanceRequest) {
        logger.info("Creating new customer.");

        // Validate createRequest fields
        validateBalanceCreate(balanceRequest);
        LocalDateTime lastUpdated = balanceRequest.getLastUpdated() != null
                ? balanceRequest.getLastUpdated()
                : LocalDateTime.now();
        Balance balance = Balance.builder()
                .accountNumber(balanceRequest.getAccountNumber())
                .currentBalance(balanceRequest.getCurrentBalance())
                .lastUpdated(balanceRequest.getLastUpdated())
                .build();
        balanceRepository.save(balance);
        logger.info("Balance {} is saved", balance.getId());
        return "Balance Save Successful";
    }

    private void validateBalanceCreate(BalanceRequest balanceRequest) {
        if (balanceRequest.getAccountNumber() == null || balanceRequest.getAccountNumber().isEmpty()) {
            throw new BalanceNotFoundException("Account number cannot be null or empty.");
        }
        if (balanceRequest.getCurrentBalance() == null || balanceRequest.getCurrentBalance() <= 0) {
            throw new BalanceNotFoundException("Amount cannot be null or less than or equal to zero.");
        }
        if (balanceRequest.getAccountNumber() == null || balanceRequest.getAccountNumber().isEmpty()) {
            throw new BalanceNotFoundException("Account number cannot be null or empty.");
        }
    }


    public BalanceResponse getBalanceByAccountNumber(String accountNumber) {
        logger.info("Fetching Balance with account number: {}", accountNumber);
        Balance balance = balanceRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BalanceNotFoundException("Balance Not found with account number" + accountNumber));
        return mapToBalanceResponse(balance);

    }

    public BalanceResponse updateBalance(String accountNumber, BalanceRequest balanceRequest) {
        Balance existingBalance = balanceRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BalanceNotFoundException("Balance not found with account number" + accountNumber));
       // validateBalanceUpdate(balanceRequest);
        if (balanceRequest.getCurrentBalance() != null && balanceRequest.getCurrentBalance() >= 0) {
            existingBalance.setCurrentBalance(balanceRequest.getCurrentBalance());
        }
        if (balanceRequest.getLastUpdated() != null ) {
            existingBalance.setLastUpdated(balanceRequest.getLastUpdated());
        }
       Balance updatedBalance = balanceRepository.save(existingBalance);
        logger.info("Balance with account number {} has been updated", accountNumber);
        return  mapToBalanceResponse(updatedBalance);
    }




    public void deleteBalance(String accountNumber) {
        Balance existingBalance = balanceRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BalanceNotFoundException("Balance not found with account number: " + accountNumber));

        balanceRepository.delete(existingBalance);
        log.info("Balance with account number {} has been deleted", accountNumber);
    }
//    public String createOrUpdateBalance(BalanceRequest balanceRequest) {
//        logger.info("Checking for existing balance with account number: {}", balanceRequest.getAccountNumber());
//
//        // Check if a balance with the given account number already exists
//        Balance existingBalance = balanceRepository.findByAccountNumber(balanceRequest.getAccountNumber()).orElse(null);
//
//        if (existingBalance != null) {
//            // Update the existing balance
//            logger.info("Balance found for account number: {}. Updating balance.", balanceRequest.getAccountNumber());
//
//            existingBalance.setCurrentBalance(balanceRequest.getCurrentBalance());
//            existingBalance.setLastUpdated(balanceRequest.getLastUpdated() != null
//                    ? balanceRequest.getLastUpdated()
//                    : LocalDateTime.now());
//            balanceRepository.save(existingBalance);
//            return "Balance updated successfully";
//        } else {
//            // Create a new balance record
//            logger.info("No balance found for account number: {}. Creating new balance.", balanceRequest.getAccountNumber());
//
//            Balance newBalance = Balance.builder()
//                    .accountNumber(balanceRequest.getAccountNumber())
//                    .currentBalance(balanceRequest.getCurrentBalance())
//                    .transactionType(balanceRequest.getTransactionType())
//                    .lastUpdated(balanceRequest.getLastUpdated() != null
//                            ? balanceRequest.getLastUpdated()
//                            : LocalDateTime.now())
//                    .build();
//            balanceRepository.save(newBalance);
//            return "New balance created successfully";
//        }
//    }

//public String createOrUpdateBalance(BalanceRequest balanceRequest) {
//    logger.info("Checking for existing balance with account number: {}", balanceRequest.getAccountNumber());
//
//    // Check if a balance with the given account number already exists
//    Balance existingBalance = balanceRepository.findByAccountNumber(balanceRequest.getAccountNumber()).orElse(null);
//
//    if (existingBalance != null) {
//        // Update the existing balance
//        logger.info("Balance found for account number: {}. Updating balance.", balanceRequest.getAccountNumber());
//
//        // Determine the transaction type and update the balance accordingly
//        if ("DEPOSIT".equalsIgnoreCase(balanceRequest.getTransactionType())) {
//            existingBalance.setCurrentBalance(existingBalance.getCurrentBalance() + balanceRequest.getCurrentBalance());
//            if(existingBalance.getTransactionType() == null){
//                existingBalance.setTransactionType("WITHDRAWAL");
//            }
//        } else if ("WITHDRAWAL".equalsIgnoreCase(balanceRequest.getTransactionType())) {
//            if (existingBalance.getCurrentBalance() >= balanceRequest.getCurrentBalance()) {
//                existingBalance.setCurrentBalance(existingBalance.getCurrentBalance() - balanceRequest.getCurrentBalance());
//                if(existingBalance.getTransactionType() == null){
//                    existingBalance.setTransactionType("WITHDRAWAL");
//                }
//            } else {
//                return "Insufficient balance for withdrawal"; // Handle insufficient balance
//            }
//        } else {
//            return "Invalid transaction type"; // Handle invalid transaction type
//        }
//
//        existingBalance.setLastUpdated(balanceRequest.getLastUpdated() != null
//                ? balanceRequest.getLastUpdated()
//                : LocalDateTime.now());
//        balanceRepository.save(existingBalance);
//        return "Balance updated successfully";
//    } else {
//        // Create a new balance record
//        logger.info("No balance found for account number: {}. Creating new balance.", balanceRequest.getAccountNumber());
//
//        Balance newBalance = Balance.builder()
//                .accountNumber(balanceRequest.getAccountNumber())
//                .currentBalance(balanceRequest.getCurrentBalance())
//                .transactionType(balanceRequest.getTransactionType())
//                .lastUpdated(balanceRequest.getLastUpdated() != null
//                        ? balanceRequest.getLastUpdated()
//                        : LocalDateTime.now())
//                .build();
//        if (balanceRequest.getTransactionType() == null || balanceRequest.getTransactionType().isEmpty()) {
//            return "Transaction type is required";
//        }
//        else {
//
//            balanceRepository.save(newBalance);
//
//            return "New balance created successfully";
//        }
//
//    }
//}

    public String createOrUpdateBalance(BalanceRequest balanceRequest) {
        logger.info("Checking for existing balance with account number: {}", balanceRequest.getAccountNumber());

        // Check if a balance with the given account number already exists
        Balance existingBalance = balanceRepository.findByAccountNumber(balanceRequest.getAccountNumber()).orElse(null);

        if (existingBalance != null) {
            // Update the existing balance
            logger.info("Balance found for account number: {}. Updating balance.", balanceRequest.getAccountNumber());

            // Update balance based on the transaction type
            if ("DEPOSIT".equalsIgnoreCase(balanceRequest.getTransactionType())) {
                existingBalance.setCurrentBalance(existingBalance.getCurrentBalance() + balanceRequest.getCurrentBalance());
            } else if ("WITHDRAWAL".equalsIgnoreCase(balanceRequest.getTransactionType())) {
                if (existingBalance.getCurrentBalance() >= balanceRequest.getCurrentBalance()) {
                    existingBalance.setCurrentBalance(existingBalance.getCurrentBalance() - balanceRequest.getCurrentBalance());
                } else {
                    return "Insufficient balance for withdrawal"; // Handle insufficient balance
                }
            } else {
                return "Invalid transaction type"; // Handle invalid transaction type
            }

            // Update transaction type if necessary
            if (existingBalance.getTransactionType() == null || existingBalance.getTransactionType().isEmpty()) {
                existingBalance.setTransactionType(balanceRequest.getTransactionType());
            }

            // Update the last updated time
            existingBalance.setLastUpdated(balanceRequest.getLastUpdated() != null
                    ? balanceRequest.getLastUpdated()
                    : LocalDateTime.now());

            balanceRepository.save(existingBalance);
            return "Balance updated successfully";
        } else {
            // Create a new balance record
            logger.info("No balance found for account number: {}. Creating new balance.", balanceRequest.getAccountNumber());

            if (balanceRequest.getTransactionType() == null || balanceRequest.getTransactionType().isEmpty()) {
                return "Transaction type is required";
            }

            Balance newBalance = Balance.builder()
                    .accountNumber(balanceRequest.getAccountNumber())
                    .currentBalance(balanceRequest.getCurrentBalance())
                    .transactionType(balanceRequest.getTransactionType())
                    .lastUpdated(balanceRequest.getLastUpdated() != null
                            ? balanceRequest.getLastUpdated()
                            : LocalDateTime.now())
                    .build();

            balanceRepository.save(newBalance);
            return "New balance created successfully";
        }
    }

}


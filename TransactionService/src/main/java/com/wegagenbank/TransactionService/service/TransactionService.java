package com.wegagenbank.TransactionService.service;

import com.wegagenbank.TransactionService.dto.TransactionRequest;
import com.wegagenbank.TransactionService.dto.TransactionResponse;
import com.wegagenbank.TransactionService.exception.TransactionNotFoundException;
import com.wegagenbank.TransactionService.model.Transaction;
import com.wegagenbank.TransactionService.repository.TransactionRepository;
import jakarta.transaction.TransactionalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    public List<TransactionResponse> getAllTransaction() {
        List<Transaction> transactions =transactionRepository.findAll();
        return transactions.stream().map(this::mapToTransactionResponse).toList();
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .accountNumber(transaction.getAccountNumber())
                .amount(transaction.getAmount())
                .transactionDate(transaction.getTransactionDate())
                .transactionType(transaction.getTransactionType())
                .transactionStatus(transaction.getTransactionStatus())
                .description(transaction.getDescription())
                .build();
    }

    public String createTransaction(TransactionRequest transactionRequest) {
        logger.info("Creating new Transaction.");

        // Validate createRequest fields
        validateTransactionCreate(transactionRequest);
        Transaction transaction = Transaction.builder()
                .accountNumber(transactionRequest.getAccountNumber())
                .amount(transactionRequest.getAmount())
                .transactionDate(transactionRequest.getTransactionDate())
                .transactionType(transactionRequest.getTransactionType())
                .transactionStatus(transactionRequest.getTransactionStatus())
                .description(transactionRequest.getDescription())
                .build();
        transactionRepository.save(transaction);
        logger.info("Transaction {} is saved", transaction.getId());
        return "Transaction Save Successful";
    }

    private void validateTransactionCreate(TransactionRequest transactionRequest) {
        if (transactionRequest.getAccountNumber() == null || transactionRequest.getAccountNumber().isEmpty()) {
            throw new TransactionNotFoundException("Account number cannot be null or empty.");
        }
        if (transactionRequest.getAmount() == null || transactionRequest.getAmount() <= 0) {
            throw new TransactionNotFoundException("Amount cannot be null or less than or equal to zero.");
        }
        if (transactionRequest.getTransactionType() == null || transactionRequest.getTransactionType().isEmpty()) {
            throw new TransactionNotFoundException("Transaction Type cannot be null or or empty.");
        }
        if (transactionRequest.getTransactionStatus() == null || transactionRequest.getTransactionStatus().isEmpty()) {
            throw new TransactionNotFoundException("Transaction Status cannot be null or or empty.");
        }
        if (transactionRequest.getTransactionDate() == null) {
            throw new TransactionNotFoundException("Transaction Date cannot be null.");
        }


    }

    public TransactionResponse getTransactionByAccountNumber(String accountNumber) {
        logger.info("Fetching transaction with account number: {}", accountNumber);

        Transaction transaction = transactionRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with account number: " + accountNumber));

        return mapToTransactionResponse(transaction);
    }

    public TransactionResponse updateTransaction(String accountNumber, TransactionRequest transactionRequest) {
        // Fetch the existing transaction by account number
        Transaction existingTransaction = transactionRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new TransactionNotFoundException("Customer not found with account number: " + accountNumber));
        // Validate the fields in the update request
        validateTransactionUpdate(transactionRequest);

        // Update only the fields that are not null or empty
        if (transactionRequest.getAmount() != null && transactionRequest.getAmount() >= 0) {
            existingTransaction.setAmount(transactionRequest.getAmount());
        }
        if (transactionRequest.getTransactionType() != null && !transactionRequest.getTransactionType().isEmpty()) {
            existingTransaction.setTransactionType(transactionRequest.getTransactionType());
        }
        if (transactionRequest.getTransactionDate() != null ) {
            existingTransaction.setTransactionDate(transactionRequest.getTransactionDate());
        }
        if (transactionRequest.getTransactionStatus() != null && !transactionRequest.getTransactionStatus().isEmpty()) {
            existingTransaction.setTransactionStatus(transactionRequest.getTransactionStatus());
        }
        if (transactionRequest.getDescription() != null && !transactionRequest.getDescription().isEmpty()) {
            existingTransaction.setDescription(transactionRequest.getDescription());
        }
        

        // Save the updated customer
        Transaction updatedTransaction = transactionRepository.save(existingTransaction);
        logger.info("Transaction with account number {} has been updated", accountNumber);

        return mapToTransactionResponse(updatedTransaction);
    }

    private void validateTransactionUpdate(TransactionRequest transactionRequest) {
        if (transactionRequest.getAccountNumber() != null && transactionRequest.getAccountNumber().length() != 13) {
            throw new TransactionNotFoundException("Account number must be 13 characters long.");
        }
        if (transactionRequest.getAmount() != null && transactionRequest.getAmount() >= 0) {
            throw new TransactionNotFoundException("Amount cannot be null or less than or equal to zero.");
        }
        if (transactionRequest.getTransactionType() != null && transactionRequest.getTransactionType().isEmpty()) {
            throw new TransactionNotFoundException("Transaction Type cannot be empty.");
        }
        if (transactionRequest.getTransactionDate() != null ) {
            throw new TransactionNotFoundException("Transaction Date cannot be null.");
        }
        if (transactionRequest.getTransactionStatus() != null && transactionRequest.getTransactionStatus().isEmpty()) {
            throw new TransactionNotFoundException("Transaction Status  cannot be null or empty.");
        }


    }

    public void deleteTransaction(String accountNumber) {
        Transaction existingTransaction = transactionRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with account number: " + accountNumber));

        transactionRepository.delete(existingTransaction);
        log.info("Transaction with account number {} has been deleted", accountNumber);
    }
}

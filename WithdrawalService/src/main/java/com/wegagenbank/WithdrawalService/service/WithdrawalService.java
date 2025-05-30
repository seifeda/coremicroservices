package com.wegagenbank.WithdrawalService.service;

import com.wegagenbank.WithdrawalService.dto.*;
import com.wegagenbank.WithdrawalService.exception.WithdrawalNotFoundException;
import com.wegagenbank.WithdrawalService.model.Withdrawal;
import com.wegagenbank.WithdrawalService.repository.WithdrawalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    private final WebClient.Builder webClientBuilder;
    private static final Logger logger = LoggerFactory.getLogger(WithdrawalService.class);

    public List<WithdrawalResponse> getAllWithdrawals() {
        List<Withdrawal> withdrawals = withdrawalRepository.findAll();
        return withdrawals.stream().map(this::mapToWithdrawalResponse).collect(Collectors.toList());
    }

    private WithdrawalResponse mapToWithdrawalResponse(Withdrawal withdrawal) {
        return WithdrawalResponse.builder()
                .id(withdrawal.getId())
                .accountNumber(withdrawal.getAccountNumber())
                .withdrawalDate(withdrawal.getWithdrawalDate())
                .amount(withdrawal.getAmount())
                .status(withdrawal.getStatus())
                .build();
    }


public String createWithdrawal(WithdrawalRequest withdrawalRequest) {
    logger.info("Creating a new withdrawal for account number: {}", withdrawalRequest.getAccountNumber());

    // Validate the withdrawal request
    validateWithdrawalCreate(withdrawalRequest);
    // Check if account is active
    String customerResponse = getAccountValidation(withdrawalRequest.getAccountNumber());
    if (customerResponse == null) {
        // Handle the case where the response is null (error or account not found)
        logger.error("Customer account not found or service error for account number: " + withdrawalRequest.getAccountNumber());
        return "Customer account not found or service error for account number: " + withdrawalRequest.getAccountNumber();
    }

    // Ensure the account is active before proceeding
    if ("Active".equals(customerResponse)) {
    // Build the Withdrawal entity
    Withdrawal withdrawal = Withdrawal.builder()
                .accountNumber(withdrawalRequest.getAccountNumber())
                .amount(withdrawalRequest.getAmount())
                .withdrawalDate(withdrawalRequest.getWithdrawalDate() != null ? withdrawalRequest.getWithdrawalDate() : LocalDateTime.now())
                .status(withdrawalRequest.getStatus())
                .build();

    // Fetch balance and validate if funds are sufficient
    BalanceResponse balanceResponse = getAccountBalance(withdrawalRequest.getAccountNumber());

    // Ensure sufficient balance
    if (balanceResponse.getCurrentBalance() >= withdrawal.getAmount()) {


        // Call updateAccountBalance after saving the withdrawal
        //ResponseEntity<BalanceResponse> updatedBalanceResponse = updateAccountBalance(balanceResponse);
        if(balanceResponse.getTransactionType()==null){
            balanceResponse.setTransactionType("WITHDRAWAL");
        }

        ResponseEntity<String> createTransaction = createTransaction(
                withdrawalRequest.getAccountNumber(),
                withdrawalRequest.getAmount(),
                "WITHDRAWAL"
        );

        if (createTransaction.getStatusCode() == HttpStatus.OK) {

            ResponseEntity<BalanceResponse> updatedBalanceResponse = updateAccountBalance(balanceResponse.getAccountNumber(), withdrawal.getAmount(), "WITHDRAWAL");


            if (updatedBalanceResponse.getStatusCode().is2xxSuccessful()) {
                withdrawalRepository.save(withdrawal);
                logger.info("Withdrawal transaction {} has been saved successfully", withdrawal.getId());
                logger.info("Account balance updated successfully for account number: {}", withdrawalRequest.getAccountNumber());
            } else {
                logger.error("Failed to update account balance for account number: {}", withdrawalRequest.getAccountNumber());
            }
        }

        return "Withdrawal transaction saved successfully.";
    } else {
        logger.error("Insufficient balance for account: {}", withdrawalRequest.getAccountNumber());
        throw new WithdrawalNotFoundException("Insufficient balance for the withdrawal");
    }
    } else {
        // Handle the case where the account is inactive
        logger.error("Customer account is inactive or not found for account number: " + withdrawalRequest.getAccountNumber());
        return "Customer account is inactive or not found for account number: " + withdrawalRequest.getAccountNumber();
    }
}
    private String getAccountValidation(String accountNumber) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/api/v1/customer/status/{accountNumber}", accountNumber)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response -> {
                        logger.error("Account not found or other client-side error for account number: " + accountNumber);
                        return Mono.error(new RuntimeException("Client error occurred for account number: " + accountNumber));
                    })
                    .onStatus(status-> status.is5xxServerError(), response -> {
                        logger.error("Server error while validating account for account number: " + accountNumber);
                        return Mono.error(new RuntimeException("Server error occurred."));
                    })
                    .bodyToMono(String.class)
                    .block();  // Blocking for synchronous call
        } catch (Exception e) {
            logger.error("Error while communicating with CustomerService", e);
            return null;  // Return null in case of exception
        }
    }

    public ResponseEntity<String> createTransaction(String accountNumber, double amount, String transactionType) {
        String url = "http://localhost:8084/api/v1/transaction/create";

        // Validate input parameters
        if (accountNumber == null || accountNumber.isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }
        if (transactionType == null || transactionType.isEmpty()) {
            throw new IllegalArgumentException("Transaction type cannot be null or empty");
        }

        // Create TransactionRequest DTO and set values based on the provided JSON structure
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAccountNumber(accountNumber);
        transactionRequest.setTransactionType(transactionType);
        transactionRequest.setAmount(amount);
        transactionRequest.setTransactionDate(LocalDateTime.now()); // Setting current time
        transactionRequest.setTransactionStatus("Completed");  // Assuming "Completed" as a default status
        transactionRequest.setDescription("Inserted from Service of Deposit");

        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TransactionRequest> requestEntity = new HttpEntity<>(transactionRequest, headers);

        try {
            RestTemplate restTemplate = new RestTemplate();

            // Logging the request details
            logger.info("Sending POST request to {} with body: {}", url, transactionRequest);

            // Make the REST call
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            logger.info("Received response: {} - {}", response.getStatusCode(), response.getBody());

            // Check the response and return accordingly
            if (response.getStatusCode() == HttpStatus.OK) {
                return new ResponseEntity<>("Transaction Save Successful", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Transaction Failed", HttpStatus.BAD_REQUEST);
            }
        } catch (RestClientException e) {
            logger.error("Failed to update account balance: {}", e.getMessage());
            throw new RuntimeException("Failed to update account balance", e);
        }
    }
    private BalanceResponse getAccountBalance(String accountNumber) {
    try {
        return webClientBuilder.build()
                .get()
                .uri("http://BalanceService/api/v1/balance/{accountNumber}", accountNumber)
                //.uri("http://localhost:8081/api/v1/balance/{accountNumber}", accountNumber)
                .retrieve()
                .bodyToMono(BalanceResponse.class)
                .block();
    } catch (Exception e) {
        logger.error("Error while communicating with BalanceService", e);
        throw new RuntimeException("Failed to retrieve account balance, please try again later.", e);
    }
}

    private ResponseEntity<BalanceResponse> updateAccountBalance(String accountNumber, double currentBalance, String transactionType) {
        try {
            // Constructing JSON request dynamically based on parameters
            String jsonRequest = String.format("{ \"accountNumber\": \"%s\", \"currentBalance\": %.2f, \"transactionType\": \"%s\" }",
                    accountNumber, currentBalance, transactionType);

            // Define the final URL
            String finalUrl = "http://BalanceService/api/v1/balance/deposit";

            // Log the final URL and the JSON request body
            logger.info("Final URL: {}", finalUrl);
            logger.info("Request Body: {}", jsonRequest);

            WebClient webClient = webClientBuilder.build();

            // Sending raw JSON string instead of BalanceRequest object
            BalanceResponse updatedBalance = webClient.post()
                    .uri(finalUrl) // Use the defined final URL
                    .contentType(MediaType.APPLICATION_JSON) // Set content type to JSON
                    .bodyValue(jsonRequest) // Send raw JSON string
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(),
                            response -> Mono.error(new WebClientResponseException(
                                    "Resource not found",
                                    response.statusCode().value(),
                                    response.statusCode().toString(),
                                    response.headers().asHttpHeaders(),
                                    null,
                                    null)))
                    .onStatus(status -> status.is5xxServerError(),
                            response -> Mono.error(new WebClientResponseException(
                                    "Server error occurred",
                                    response.statusCode().value(),
                                    response.statusCode().toString(),
                                    response.headers().asHttpHeaders(),
                                    null,
                                    null)))
                    .bodyToMono(BalanceResponse.class) // Parse response to BalanceResponse
                    .block();

            return ResponseEntity.ok(updatedBalance);

        } catch (WebClientResponseException e) {
            logger.error("Error while communicating with BalanceService: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (Exception e) {
            logger.error("General error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private void validateWithdrawalCreate(WithdrawalRequest withdrawalRequest) {
        if (withdrawalRequest.getAccountNumber() == null || withdrawalRequest.getAccountNumber().isEmpty()) {
            throw new WithdrawalNotFoundException("Account number cannot be null or empty.");
        }
        if (withdrawalRequest.getAmount() == null || withdrawalRequest.getAmount() <= 0) {
            throw new WithdrawalNotFoundException("Withdrawal amount must be greater than 0.");
        }
        if (withdrawalRequest.getStatus() == null || withdrawalRequest.getStatus().isEmpty()) {
            throw new WithdrawalNotFoundException("Status cannot be null or empty.");
        }
    }

    public List<WithdrawalResponse> getWithdrawalsByAccountNumber(String accountNumber) {
        logger.info("Fetching withdrawals for account number: {}", accountNumber);
        List<Withdrawal> withdrawals = withdrawalRepository.findByAccountNumber(accountNumber);

        if (withdrawals.isEmpty()) {
            throw new WithdrawalNotFoundException("No withdrawals found for account number: " + accountNumber);
        }

        return withdrawals.stream().map(this::mapToWithdrawalResponse).collect(Collectors.toList());
    }

    public WithdrawalResponse updateWithdrawal(Long id, WithdrawalRequest withdrawalRequest) {
        Withdrawal existingWithdrawal = withdrawalRepository.findById(id)
                .orElseThrow(() -> new WithdrawalNotFoundException("Withdrawal not found with ID: " + id));

        // Update the fields based on the request
        updateWithdrawalDetails(existingWithdrawal, withdrawalRequest);

        Withdrawal updatedWithdrawal = withdrawalRepository.save(existingWithdrawal);
        logger.info("Withdrawal with ID {} has been updated successfully", id);

        return mapToWithdrawalResponse(updatedWithdrawal);
    }

    private void updateWithdrawalDetails(Withdrawal existingWithdrawal, WithdrawalRequest withdrawalRequest) {
        if (withdrawalRequest.getAccountNumber() != null && !withdrawalRequest.getAccountNumber().isEmpty()) {
            existingWithdrawal.setAccountNumber(withdrawalRequest.getAccountNumber());
        }
        if (withdrawalRequest.getAmount() != null && withdrawalRequest.getAmount() > 0) {
            existingWithdrawal.setAmount(withdrawalRequest.getAmount());
        }
        if (withdrawalRequest.getWithdrawalDate() != null) {
            existingWithdrawal.setWithdrawalDate(withdrawalRequest.getWithdrawalDate());
        }
        if (withdrawalRequest.getStatus() != null && !withdrawalRequest.getStatus().isEmpty()) {
            existingWithdrawal.setStatus(withdrawalRequest.getStatus());
        }
    }

    public void deleteWithdrawal(Long id) {
        Withdrawal withdrawal = withdrawalRepository.findById(id)
                .orElseThrow(() -> new WithdrawalNotFoundException("Withdrawal not found with ID: " + id));
        withdrawalRepository.delete(withdrawal);
        logger.info("Withdrawal with ID {} has been deleted", id);
    }
}

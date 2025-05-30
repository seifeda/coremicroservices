package com.wegagenbank.DepositService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wegagenbank.DepositService.dto.*;
import com.wegagenbank.DepositService.exception.DepositNotFoundException;
import com.wegagenbank.DepositService.model.Deposit;
import com.wegagenbank.DepositService.repository.DepositRepository;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepositService {
    private final DepositRepository depositRepository;
    private final WebClient.Builder webClientBuilder;
    private static final Logger logger = LoggerFactory.getLogger(DepositService.class);

    public List<DepositResponse> getAllDeposit() {
            List<Deposit> deposits =depositRepository.findAll();
            return deposits.stream().map(this::mapToDepositResponse).toList();

    }

    private DepositResponse mapToDepositResponse(Deposit deposit) {
        return DepositResponse.builder()
                .id(deposit.getId())
                .accountNumber(deposit.getAccountNumber())
                .depositDate(deposit.getDepositDate())
                .amount(deposit.getAmount())
                .status(deposit.getStatus())
                .build();
    }

    public String createDeposit(DepositRequest depositRequest) {
        logger.info("Creating new deposit.");

        // Validate the deposit request
        validateDepositCreate(depositRequest);

        // Check if account is active
        String customerResponse = getAccountValidation(depositRequest.getAccountNumber());

        if (customerResponse == null) {
            // Handle the case where the response is null (error or account not found)
            logger.error("Customer account not found or service error for account number: " + depositRequest.getAccountNumber());
            return "Customer account not found or service error for account number: " + depositRequest.getAccountNumber();
        }

        // Ensure the account is active before proceeding
        if ("Active".equals(customerResponse)) {

            // Now that the account is validated, create the Deposit object
            Deposit deposit = Deposit.builder()
                    .accountNumber(depositRequest.getAccountNumber())
                    .amount(depositRequest.getAmount())
                    .depositDate(depositRequest.getDepositDate())
                    .status(depositRequest.getStatus())
                    .build();

            // Set default transaction type if not provided
            if (depositRequest.getTransactionType() == null) {
                depositRequest.setTransactionType("DEPOSIT");
            }

            // Update the account balance
            ResponseEntity<String> updatedBalanceResponse = updateAccountBalance(
                    depositRequest.getAccountNumber(),
                    depositRequest.getAmount(),
                    "DEPOSIT"
            );

            // Create the deposit transaction
            ResponseEntity<String> createTransaction = createTransaction(
                    depositRequest.getAccountNumber(),
                    depositRequest.getAmount(),
                    "DEPOSIT"
            );

            // Check the response status and handle success or failure
            if (updatedBalanceResponse.getStatusCode() == HttpStatus.OK) {
                if (createTransaction.getStatusCode() == HttpStatus.OK) {
                    // Successful deposit, save the deposit record
                    depositRepository.save(deposit);
                    logger.info("Deposit Transaction {} is saved", deposit.getId());
                    return "Deposit Transaction Save Successful: " + updatedBalanceResponse.getBody();
                } else {
                    logger.error("Failed to create transaction. Status code: " + createTransaction.getStatusCode());
                    return "Failed to create transaction: " + createTransaction.getBody();
                }
            } else {
                logger.error("Failed to update balance. Status code: " + updatedBalanceResponse.getStatusCode());
                return "Failed to update balance: " + updatedBalanceResponse.getBody();
            }

        } else {
            // Handle the case where the account is inactive
            logger.error("Customer account is inactive or not found for account number: " + depositRequest.getAccountNumber());
            return "Customer account is inactive or not found for account number: " + depositRequest.getAccountNumber();
        }
    }





    public ResponseEntity<String> updateAccountBalance(String accountNumber, double currentBalance, String transactionType) {
        String url = "http://localhost:8090/api/v1/balance/deposit";

        // Validate input parameters
        if (accountNumber == null || accountNumber.isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }
        if (transactionType == null || transactionType.isEmpty()) {
            throw new IllegalArgumentException("Transaction type cannot be null or empty");
        }

        // Create balance request DTO
        BalanceRequest balanceRequest = new BalanceRequest();
        balanceRequest.setAccountNumber(accountNumber);
        balanceRequest.setCurrentBalance(currentBalance);
        balanceRequest.setTransactionType(transactionType);
        balanceRequest.setLastUpdated(LocalDateTime.now());

        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BalanceRequest> requestEntity = new HttpEntity<>(balanceRequest, headers);

        try {
            RestTemplate restTemplate = new RestTemplate();

            // Logging the request details
            logger.info("Sending POST request to {} with body: {}", url, balanceRequest);

            // Make the REST call
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            System.out.println("Received response: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            return response;  // Return the plain text response directly
        } catch (RestClientException e) {
            System.out.println("Failed to update account balance: " + e.getMessage());
            throw new RuntimeException("Failed to update account balance", e);
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




    private void validateDepositCreate(DepositRequest depositRequest) {
        if (depositRequest.getAccountNumber() == null || depositRequest.getAccountNumber().isEmpty()) {
            throw new DepositNotFoundException("Account number cannot be null or empty.");
        }
        if(depositRequest.getDepositDate()==null){
            throw new DepositNotFoundException("Deposit Date cannot be null.");
        }
        if(depositRequest.getStatus() == null || depositRequest.getStatus().isEmpty()){
            throw new DepositNotFoundException("Status cannot be null or empty.");
        }
        if(depositRequest.getAmount() == null || depositRequest.getAmount()<=0){
            throw new DepositNotFoundException("Amount cannot be null or 0.");
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





        public List<DepositResponse> getDepositsByAccountNumber(String accountNumber) {
        logger.info("Fetching Deposits with account number: {}", accountNumber);

        // Fetch deposits by account number
        List<Deposit> deposits = depositRepository.findByAccountNumber(accountNumber);

        // If no deposits found, throw an exception
        if (deposits.isEmpty()) {
            throw new DepositNotFoundException("No deposits found for account number: " + accountNumber);
        }

        // Map each Deposit entity to DepositResponse and return the list
        return deposits.stream()
                .map(this::mapToDepositResponse)
                .collect(Collectors.toList());
    }

    public DepositResponse updateDeposit(Long id, DepositRequest depositRequest) {
        Deposit existingDeposit = depositRepository.findById(id)
                .orElseThrow(() -> new DepositNotFoundException("Deposit not found with ID: " + id));

        // Validate the fields in the update request
        validateDepositUpdate(depositRequest);

        // Update only the fields that are not null and valid
        if (depositRequest.getAccountNumber() != null && !depositRequest.getAccountNumber().isEmpty()) {
            existingDeposit.setAccountNumber(depositRequest.getAccountNumber());
        }
        if (depositRequest.getAmount() != null && depositRequest.getAmount() > 0) {
            existingDeposit.setAmount(depositRequest.getAmount());
        }
        if (depositRequest.getDepositDate() != null) {
            existingDeposit.setDepositDate(depositRequest.getDepositDate());
        }
        if (depositRequest.getStatus() != null && !depositRequest.getStatus().isEmpty()) {
            existingDeposit.setStatus(depositRequest.getStatus());
        }

        // Save the updated deposit
        Deposit updatedDeposit = depositRepository.save(existingDeposit);
        logger.info("Deposit with ID {} has been updated", id);

        return mapToDepositResponse(updatedDeposit);
    }

    private void validateDepositUpdate(DepositRequest depositRequest) {
        if (depositRequest.getAccountNumber() == null || depositRequest.getAccountNumber().length() != 13) {
            throw new DepositNotFoundException("Account number must be 13 characters long.");
        }
        if (depositRequest.getStatus() == null || depositRequest.getStatus().isEmpty()) {
            throw new DepositNotFoundException("Deposit Transaction Status cannot be null or Empty");
        }
        if (depositRequest.getDepositDate() == null ) {
            throw new DepositNotFoundException("Deposit Transaction date cannot be null or Empty");
        }
        if (depositRequest.getAmount() == null || depositRequest.getAmount()<= 0) {
            throw new DepositNotFoundException("Deposit Transaction amount cannot be null or Empty");
        }

    }

    public void deleteDeposit(Long id) {
        Deposit existingDeposit =depositRepository.findById(id)
                .orElseThrow(() -> new DepositNotFoundException("Deposit Transaction not found with id: " + id));
        depositRepository.delete(existingDeposit);
        log.info("Deposit Transaction with id {} has been deleted", id);


    }
}

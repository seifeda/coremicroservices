package com.wegagenbank.CustomerService.service;

import com.wegagenbank.CustomerService.dto.CustomerRequest;
import com.wegagenbank.CustomerService.dto.CustomerResponse;
import com.wegagenbank.CustomerService.exception.CustomerNotFoundException;
import com.wegagenbank.CustomerService.model.Customer;
import com.wegagenbank.CustomerService.repository.CustomerRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
private final CustomerRepository customerRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    public List<CustomerResponse> getAllCustomer() {
  List<Customer> customers =customerRepository.findAll();
  return customers.stream().map(this::mapToCustomerResponse).toList();
    }


    private CustomerResponse mapToCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .accountNumber(customer.getAccountNumber())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .address(customer.getAddress())
                .dateOfBirth(customer.getDateOfBirth())
                .accountType(customer.getAccountType())
                .status(customer.getStatus())
                .build();
    }

    public String createCustomer(CustomerRequest customerRequest) {
        logger.info("Creating new customer.");

        // Validate createRequest fields
        validateCustomerCreate(customerRequest);

        Customer customer = Customer.builder()
                .accountNumber(customerRequest.getAccountNumber())
                .firstName(customerRequest.getFirstName())
                .lastName(customerRequest.getLastName())
                .email(customerRequest.getEmail())
                .phoneNumber(customerRequest.getPhoneNumber())
                .address(customerRequest.getAddress())
                .dateOfBirth(customerRequest.getDateOfBirth())
                .accountType(customerRequest.getAccountType())
                .status(customerRequest.getStatus())
                .build();

        customerRepository.save(customer);
        logger.info("Customer {} is saved", customer.getId());
        return "Customer Save Successful";
    }

    public CustomerResponse updateCustomer(String accountNumber, CustomerRequest customerRequest) {
        // Fetch the existing customer by account number
        Customer existingCustomer = customerRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with account number: " + accountNumber));

        // Validate the fields in the update request
        validateCustomerUpdate(customerRequest);

        // Update only the fields that are not null or empty
        if (customerRequest.getFirstName() != null && !customerRequest.getFirstName().isEmpty()) {
            existingCustomer.setFirstName(customerRequest.getFirstName());
        }
        if (customerRequest.getLastName() != null && !customerRequest.getLastName().isEmpty()) {
            existingCustomer.setLastName(customerRequest.getLastName());
        }
        if (customerRequest.getEmail() != null && !customerRequest.getEmail().isEmpty()) {
            existingCustomer.setEmail(customerRequest.getEmail());
        }
        if (customerRequest.getPhoneNumber() != null && !customerRequest.getPhoneNumber().isEmpty()) {
            existingCustomer.setPhoneNumber(customerRequest.getPhoneNumber());
        }
        if (customerRequest.getAddress() != null && !customerRequest.getAddress().isEmpty()) {
            existingCustomer.setAddress(customerRequest.getAddress());
        }
        if (customerRequest.getDateOfBirth() != null) {
            existingCustomer.setDateOfBirth(customerRequest.getDateOfBirth());
        }
        if (customerRequest.getAccountType() != null && !customerRequest.getAccountType().isEmpty()) {
            existingCustomer.setAccountType(customerRequest.getAccountType());
        }
        if (customerRequest.getStatus() != null && !customerRequest.getStatus().isEmpty()) {
            existingCustomer.setStatus(customerRequest.getStatus());
        }

        // Save the updated customer
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        logger.info("Customer with account number {} has been updated", accountNumber);

        return mapToCustomerResponse(updatedCustomer);
    }

    private void validateCustomerUpdate(CustomerRequest updateRequest) {
        if (updateRequest.getAccountNumber() != null && updateRequest.getAccountNumber().length() != 13) {
            throw new CustomerNotFoundException("Account number must be 13 characters long.");
        }
        if (updateRequest.getFirstName() != null && updateRequest.getFirstName().isEmpty()) {
            throw new CustomerNotFoundException("First name cannot be empty.");
        }
        if (updateRequest.getLastName() != null && updateRequest.getLastName().isEmpty()) {
            throw new CustomerNotFoundException("Last name cannot be empty.");
        }
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new CustomerNotFoundException("Email is invalid.");
        }
        if (updateRequest.getPhoneNumber() != null && updateRequest.getPhoneNumber().isEmpty()) {
            throw new CustomerNotFoundException("Phone number cannot be empty.");
        }
        if (updateRequest.getAddress() != null && updateRequest.getAddress().isEmpty()) {
            throw new CustomerNotFoundException("Address cannot be null or empty.");
        }
        if (updateRequest.getAccountType() != null && updateRequest.getAccountType().isEmpty()) {
            throw new CustomerNotFoundException("Account type cannot be null or empty.");
        }
        if (updateRequest.getStatus() != null && updateRequest.getStatus().isEmpty()) {
            throw new CustomerNotFoundException("Status cannot be null or empty.");
        }
    }
    private void validateCustomerCreate(CustomerRequest createRequest) {
        if (createRequest.getAccountNumber() == null || createRequest.getAccountNumber().isEmpty()) {
            throw new CustomerNotFoundException("Account number cannot be null or empty.");
        }
        if(createRequest.getAccountNumber().length() !=13 ){
            throw new CustomerNotFoundException("Account number length must be 13 digit.");
        }
        if (createRequest.getFirstName() == null || createRequest.getFirstName().isEmpty()) {
            throw new CustomerNotFoundException("First name cannot be null or empty.");
        }
        if (createRequest.getLastName() == null || createRequest.getLastName().isEmpty()) {
            throw new CustomerNotFoundException("Last name cannot be null or empty.");
        }
        if (createRequest.getEmail() == null || !createRequest.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new CustomerNotFoundException("Email is invalid.");
        }
        if (createRequest.getPhoneNumber() == null || createRequest.getPhoneNumber().isEmpty()) {
            throw new CustomerNotFoundException("Phone number cannot be null or empty.");
        }
        if (createRequest.getAddress() == null || createRequest.getAddress().isEmpty()) {
            throw new CustomerNotFoundException("Address cannot be null or empty.");
        }
        if (createRequest.getDateOfBirth() == null) {
            throw new CustomerNotFoundException("Date of birth cannot be null.");
        }
        if (createRequest.getAccountType() == null || createRequest.getAccountType().isEmpty()) {
            throw new CustomerNotFoundException("Account type cannot be null or empty.");
        }
        if (createRequest.getStatus() == null || createRequest.getStatus().isEmpty()) {
            throw new CustomerNotFoundException("Status cannot be null or empty.");
        }
    }
    public CustomerResponse getCustomerByAccountNumber(String accountNumber) {
        logger.info("Fetching customer with account number: {}", accountNumber);

        Customer customer = customerRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with account number: " + accountNumber));

        return mapToCustomerResponse(customer);
    }

    public void deleteCustomer(String accountNumber) {
        Customer existingCustomer = customerRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with account number: " + accountNumber));

        customerRepository.delete(existingCustomer);
        log.info("Customer with account number {} has been deleted", accountNumber);
    }

//    private void validateCustomerUpdate(CustomerRequest updateRequest) {
//        if (updateRequest.getAccountNumber() != null && (updateRequest.getAccountNumber().length() != 13)) {
//            throw new ValidationException("Account number must be 13 characters long.");
//        }
//        if (updateRequest.getFirstName() != null && (updateRequest.getFirstName().isEmpty())){
//            throw new ValidationException("First Name  cannot be empty.");
//        }
//        if(updateRequest.getLastName() !=null && (updateRequest.getLastName().isEmpty())){
//            throw new ValidationException("Last Name  cannot be empty.");
//        }
//
//        if (updateRequest.getEmail() != null && !updateRequest.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
//            throw new ValidationException("Email is invalid.");
//        }
//        if (updateRequest.getPhoneNumber() != null && updateRequest.getPhoneNumber().isEmpty()) {
//            throw new ValidationException("Phone number cannot be empty.");
//        }
//        if (updateRequest.getAddress() != null &&( updateRequest.getAddress().isEmpty())) {
//            throw new ValidationException("Address cannot be null or empty.");
//        }
//        if (updateRequest.getDateOfBirth() != null) {
//            throw new ValidationException("Date of birth cannot be null.");
//        }
//        if (updateRequest.getAccountType() != null && (updateRequest.getAccountType().isEmpty())) {
//            throw new ValidationException("Account type cannot be null or empty.");
//        }
//        if (updateRequest.getStatus() != null && (updateRequest.getStatus().isEmpty())) {
//            throw new ValidationException("Status cannot be null or empty.");
//        }
//
//    }

//    private void validateCustomerCreate(CustomerRequest createRequest) {
//        if (createRequest.getAccountNumber() == null || createRequest.getAccountNumber().isEmpty()) {
//            throw new ValidationException("Account number cannot be null or empty.");
//        }
//        if (createRequest.getFirstName() == null || createRequest.getFirstName().isEmpty()) {
//            throw new ValidationException("First name cannot be null or empty.");
//        }
//        if (createRequest.getLastName() == null || createRequest.getLastName().isEmpty()) {
//            throw new ValidationException("Last name cannot be null or empty.");
//        }
//        if (createRequest.getEmail() == null || !createRequest.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
//            throw new ValidationException("Email is invalid.");
//        }
//        if (createRequest.getPhoneNumber() == null || createRequest.getPhoneNumber().isEmpty()) {
//            throw new ValidationException("Phone number cannot be null or empty.");
//        }
//        if (createRequest.getAddress() == null || createRequest.getAddress().isEmpty()) {
//            throw new ValidationException("Address cannot be null or empty.");
//        }
//        if (createRequest.getDateOfBirth() == null) {
//            throw new ValidationException("Date of birth cannot be null.");
//        }
//        if (createRequest.getAccountType() == null || createRequest.getAccountType().isEmpty()) {
//            throw new ValidationException("Account type cannot be null or empty.");
//        }
//        if (createRequest.getStatus() == null || createRequest.getStatus().isEmpty()) {
//            throw new ValidationException("Status cannot be null or empty.");
//        }
//    }

}

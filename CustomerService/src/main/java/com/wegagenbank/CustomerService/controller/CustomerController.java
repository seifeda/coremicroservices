package com.wegagenbank.CustomerService.controller;


import com.wegagenbank.CustomerService.dto.CustomerRequest;
import com.wegagenbank.CustomerService.dto.CustomerResponse;
import com.wegagenbank.CustomerService.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
private final CustomerService customerService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CustomerResponse> getAllCustomer(){
        return customerService.getAllCustomer();

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createCustomer(@RequestBody CustomerRequest customerRequest){
        customerService.createCustomer(customerRequest );
    }
    @GetMapping("/{accountNumber}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CustomerResponse> getCustomerByAccountNumber(@PathVariable String accountNumber) {
        CustomerResponse customerResponse = customerService.getCustomerByAccountNumber(accountNumber);

        // Check if the customer exists
        if (customerResponse == null) {
            // Return 404 Not Found if no customer is found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Return the customer response with 200 OK if found
        return new ResponseEntity<>(customerResponse, HttpStatus.OK);
    }
    @GetMapping("/status/{accountNumber}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getAccountStatus(@PathVariable String accountNumber) {
        CustomerResponse customerResponse = customerService.getCustomerByAccountNumber(accountNumber);

        // Check if the customer exists
        if (customerResponse == null) {
            // Return 404 Not Found if no customer is found
            return new ResponseEntity<>("Customer not found", HttpStatus.NOT_FOUND);
        }

        // Check the status of the customer
        String status = customerResponse.getStatus();
        if ("Active".equals(status)) {
            // Return 200 OK with "Active" status
            return new ResponseEntity<>("Active", HttpStatus.OK);
        } else if (status == null) {
            // Return 200 OK with "Status is null" if status is null
            return new ResponseEntity<>("Status is null", HttpStatus.OK);
        }

        // Return the status if it is not "Active" or null
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PutMapping("/{accountNumber}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerResponse updateCustomer(@PathVariable String accountNumber, @RequestBody CustomerRequest customerRequest) {
        return customerService.updateCustomer(accountNumber, customerRequest);
    }
    @DeleteMapping("/{accountNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable String accountNumber) {
        customerService.deleteCustomer(accountNumber);
    }


}

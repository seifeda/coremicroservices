package com.wegagenbank.apigateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


import java.nio.charset.StandardCharsets;

@ControllerAdvice
public class GlobalGatewayExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Mono<Void> handleGatewayError(ServerWebExchange exchange, Exception ex) {
        // Set the status code based on the exception type or default to INTERNAL_SERVER_ERROR
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // You can set a different status if needed based on the exception type
//        if (ex instanceof WithdrawalNotFoundException) {
//            status = HttpStatus.NOT_FOUND;
//        }

        // Set the response status code
        exchange.getResponse().setStatusCode(status);

        // Create the error message with the exception's message
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred";

        // Set the response content type to JSON (or whatever is appropriate)
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Write the error message to the response body
        return exchange.getResponse().writeWith(Mono.just(
                exchange.getResponse().bufferFactory().wrap(errorMessage.getBytes(StandardCharsets.UTF_8))
        ));
    }
}

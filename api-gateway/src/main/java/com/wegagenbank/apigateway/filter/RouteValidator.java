package com.wegagenbank.apigateway.filter;


import org.springframework.http.server.reactive.ServerHttpRequest;
        import org.springframework.stereotype.Component;

        import java.util.*;
        import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/api/auth/register",
            "/api/auth/authenticate",
            "/api/auth/validate",
            "/eureka/web"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}
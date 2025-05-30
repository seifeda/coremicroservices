package com.wegagenbank.securityservice.auth;

import com.wegagenbank.securityservice.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
private final AuthenticationService service;
private final JwtService jwtService;
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ){
        return ResponseEntity.ok(service.register(request));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(service.authenticate(request));
    }
    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        System.out.println("Before Validate access...!");
        service.validateToken(token);
        return "Token is valid";
    }
}

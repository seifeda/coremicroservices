package com.wegagenbank.discoveryserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${eureka_username}")
    private String username;

    @Value("${eureka_password}")
    private String password;

    // Configuring authentication with in-memory users
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername(username)
                .password(passwordEncoder().encode(password))
                .authorities("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    // Password encoder
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance();  // Using NoOpPasswordEncoder for simplicity, but it's better to use bcrypt in production
//    }
    // Password encoder using bcrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Using BCryptPasswordEncoder for secure password hashing
    }

    // Configuring HTTP security
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())  // Disabling CSRF
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()  // All requests need to be authenticated
                )
                .httpBasic(withDefaults());  // Enabling HTTP Basic authentication

        return httpSecurity.build();
    }
}

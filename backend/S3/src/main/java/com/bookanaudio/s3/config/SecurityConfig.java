package com.bookanaudio.books.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Define the password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Define the security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Allow all requests
        http
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()  // Allow all requests
            )
            .csrf().disable();  // Disable CSRF for simplicity (only for development)

        return http.build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

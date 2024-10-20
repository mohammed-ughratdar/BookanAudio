package com.bookanaudio.auth.service;

import com.bookanaudio.auth.dto.AuthResponse;
import com.bookanaudio.auth.dto.LoginRequest;
import com.bookanaudio.auth.dto.RegisterRequest;
import com.bookanaudio.auth.exception.AuthException;
import com.bookanaudio.auth.model.User;
import com.bookanaudio.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername());
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid credentials");
        }
        String token = generateToken(user); 
        return new AuthResponse(token, user.getUsername());
    }

    public void register(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()) != null) {
            throw new AuthException("Username already exists");
        }
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        userRepository.save(user);
    }

    private String generateToken(User user) {
        return "token"; 
    }
}

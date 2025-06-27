package com.bookanaudio.auth.controller;

import com.bookanaudio.auth.dto.AuthResponse;
import com.bookanaudio.auth.dto.LoginRequest;
import com.bookanaudio.auth.dto.RegisterRequest;
import com.bookanaudio.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping("/oauth/callback")
    public RedirectView oauthLogin(@RequestParam("code") String authorizationCode) {
        return authService.oauthLogin(authorizationCode);
    }
}

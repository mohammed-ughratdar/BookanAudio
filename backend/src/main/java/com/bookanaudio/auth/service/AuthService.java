package com.bookanaudio.auth.service;

import com.bookanaudio.auth.dto.AuthResponse;
import com.bookanaudio.auth.dto.LoginRequest;
import com.bookanaudio.auth.dto.RegisterRequest;
import com.bookanaudio.auth.exception.AuthException;
import com.bookanaudio.auth.model.User;
import com.bookanaudio.auth.model.OAuthUser;
import com.bookanaudio.auth.repository.UserRepository;
import com.bookanaudio.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;



@Service
public class AuthService {

    @Value("${google.client_id}")
    private String clientId;

    @Value("${google.client_secret}")
    private String clientSecret;

    @Value("${google.redirect_uri}")
    private String redirectUri;

    @Value("${google.token_endpoint}")
    private String tokenEndpoint;

    @Value("${google.user_info_endpoint}")
    private String userInfoEndpoint;

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername());
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getUsername());
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

    public AuthResponse oauthLogin(String authorizationCode) {

        String accessToken = fetchAccessToken(authorizationCode);
        OAuthUser oAuthUser = fetchOAuthUserInfo(accessToken);

        User user = userRepository.findByEmail(oAuthUser.getEmail());

        if (user == null) {
            user = registerOauthUser(oAuthUser);
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername());
    }

    public User registerOauthUser(OAuthUser oAuthUser) {
        String username = generateUsername(oAuthUser.getEmail());

        User user = new User();
        user.setEmail(oAuthUser.getEmail());
        user.setUsername(username);

        userRepository.save(user);

        return user;
    }

    private String fetchAccessToken(String authorizationCode) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("code", authorizationCode);
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);
        requestBody.put("redirect_uri", redirectUri);
        requestBody.put("grant_type", "authorization_code");

        Map<String, Object> response = restTemplate.postForObject(tokenEndpoint, requestBody, Map.class);

        if (response != null && response.containsKey("access_token"))
            return response.get("access_token").toString();

        throw new RuntimeException("Failed to retrieve access token from Google");

    }

    private OAuthUser fetchOAuthUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<OAuthUser> response = restTemplate.exchange(userInfoEndpoint, HttpMethod.GET, entity, OAuthUser.class);
        return response.getBody();
    }

    private String generateUsername(String email) {
        return email.split("@")[0] + "_user";
    }

}

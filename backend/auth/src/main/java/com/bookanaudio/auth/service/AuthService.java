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
import org.springframework.web.servlet.view.RedirectView;
import java.util.HashMap;
import java.util.Map;



@Service
public class AuthService {

    @Value("${google_client_id}")
    private String clientId;

    @Value("${google_client_secret}")
    private String clientSecret;

    @Value("${google_redirect_uri}")
    private String redirectUri;

    @Value("${google_token_endpoint}")
    private String tokenEndpoint;

    @Value("${google_user_info_endpoint}")
    private String userInfoEndpoint;

    @Value("${frontend_oauth_base_url}")
    private String frontendOAuthBaseURL;

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

    public RedirectView oauthLogin(String authorizationCode) {

        String accessToken = fetchAccessToken(authorizationCode);
        OAuthUser oAuthUser = fetchOAuthUserInfo(accessToken);

        User user = userRepository.findByEmail(oAuthUser.getEmail());

        if (user == null) {
            user = registerOauthUser(oAuthUser);
        }

        String jwtToken = jwtUtil.generateToken(user.getUsername());
        String redirectUrl = frontendOAuthBaseURL + "?token=" + jwtToken + "&username=" + user.getUsername();
        return new RedirectView(redirectUrl);
    }

    public User registerOauthUser(OAuthUser oAuthUser) {
        String username = generateUsername(oAuthUser.getEmail());

        User user = new User();
        user.setEmail(oAuthUser.getEmail());
        user.setUsername(username);

        userRepository.save(user);

        return user;
    }

    protected String fetchAccessToken(String authorizationCode) {
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

    protected OAuthUser fetchOAuthUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<OAuthUser> response = restTemplate.exchange(userInfoEndpoint, HttpMethod.GET, entity, OAuthUser.class);
        return response.getBody();
    }

    protected String generateUsername(String email) {
        return email.split("@")[0] + "_user";
    }

}

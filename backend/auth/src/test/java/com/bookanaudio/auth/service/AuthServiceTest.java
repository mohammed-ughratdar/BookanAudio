package com.bookanaudio.auth.service;

import com.bookanaudio.auth.dto.AuthResponse;
import com.bookanaudio.auth.dto.LoginRequest;
import com.bookanaudio.auth.dto.RegisterRequest;
import com.bookanaudio.auth.exception.AuthException;
import com.bookanaudio.auth.model.OAuthUser;
import com.bookanaudio.auth.model.User;
import com.bookanaudio.auth.repository.UserRepository;
import com.bookanaudio.auth.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.servlet.view.RedirectView;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private UserRepository userRepository;
    private JwtUtil jwtUtil;
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        jwtUtil = mock(JwtUtil.class);
        authService = Mockito.spy(new AuthService(userRepository, jwtUtil));
    }

    @Test
    public void testLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("password123");

        User user = new User();
        user.setUsername("john");
        user.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("password123"));

        when(userRepository.findByUsername("john")).thenReturn(user);
        when(jwtUtil.generateToken("john")).thenReturn("mocked_token");

        AuthResponse response = authService.login(request);

        assertEquals("mocked_token", response.getToken());
        assertEquals("john", response.getUsername());
    }

    @Test
    public void testLogin_InvalidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("wrongpass");

        when(userRepository.findByUsername("john")).thenReturn(null);

        assertThrows(AuthException.class, () -> authService.login(request));
    }

    @Test
    public void testRegister_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setPassword("pass123");
        request.setEmail("john@example.com");

        when(userRepository.findByUsername("john")).thenReturn(null);

        authService.register(request);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegister_DuplicateUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setPassword("pass123");
        request.setEmail("john@example.com");

        when(userRepository.findByUsername("john")).thenReturn(new User());

        assertThrows(AuthException.class, () -> authService.register(request));
    }

    @Test
    public void testOauthLogin_NewUser() {
        String code = "auth-code";
        OAuthUser oAuthUser = new OAuthUser();
        oAuthUser.setEmail("newuser@example.com");

        doReturn("access-token").when(authService).fetchAccessToken(code);
        doReturn(oAuthUser).when(authService).fetchOAuthUserInfo("access-token");
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(null);
        when(jwtUtil.generateToken(any())).thenReturn("mocked_token");

        RedirectView response = authService.oauthLogin(code);

        String actualUrl = response.getUrl();
        String expectedSuffix = "?token=mocked_token&username=newuser_user";
        System.out.println("Actual redirect URL: " + actualUrl);

        assertNotNull(actualUrl);
        assertTrue(actualUrl.endsWith(expectedSuffix), "Redirect URL should end with token and username");

    }

    @Test
    public void testRegisterOauthUser() {
        OAuthUser oAuthUser = new OAuthUser();
        oAuthUser.setEmail("someone@example.com");

        User user = authService.registerOauthUser(oAuthUser);

        assertEquals("someone@example.com", user.getEmail());
        assertEquals("someone_user", user.getUsername());

        verify(userRepository).save(user);
    }
}

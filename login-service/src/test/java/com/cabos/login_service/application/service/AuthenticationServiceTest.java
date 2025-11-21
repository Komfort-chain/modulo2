package com.cabos.login_service.application.service;

import com.cabos.login_service.application.dto.LoginRequest;
import com.cabos.login_service.application.dto.RegisterRequest;
import com.cabos.login_service.domain.model.User;
import com.cabos.login_service.domain.repository.UserRepository;
import com.cabos.login_service.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private UserRepository userRepository;
    private JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;
    private AuthenticationService service;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        jwtUtil = mock(JwtUtil.class);
        passwordEncoder = mock(PasswordEncoder.class);
        service = new AuthenticationService(userRepository, jwtUtil, passwordEncoder);
    }

    @Test
    void deveAutenticarUsuario() {
        LoginRequest req = new LoginRequest("alan", "123");

        User user = new User();
        user.setUsername("alan");
        user.setPassword("encoded");

        when(userRepository.findByUsername("alan")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123", "encoded")).thenReturn(true);
        when(jwtUtil.generate("alan", null)).thenReturn("token123");

        var response = service.authenticate(req);

        assertEquals("token123", response.token());
    }

    @Test
    void deveFalharSenhaIncorreta() {
        LoginRequest req = new LoginRequest("alan", "errada");

        User user = new User();
        user.setPassword("encoded");

        when(userRepository.findByUsername("alan")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("errada", "encoded")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.authenticate(req));
    }

    @Test
    void deveRegistrarUsuario() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("novo");
        req.setPassword("123");
        req.setRole("USER");

        User saved = new User();
        saved.setUsername("novo");
        saved.setRole("USER");

        when(passwordEncoder.encode("123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        var result = service.register(req);

        assertEquals("novo", result.getUsername());
    }
}

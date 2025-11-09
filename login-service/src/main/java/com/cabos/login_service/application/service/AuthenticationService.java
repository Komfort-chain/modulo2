package com.cabos.login_service.application.service;

import com.cabos.login_service.application.dto.LoginRequest;
import com.cabos.login_service.application.dto.LoginResponse;
import com.cabos.login_service.application.dto.RegisterRequest;
import com.cabos.login_service.domain.repository.UserRepository;
import com.cabos.login_service.infrastructure.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.cabos.login_service.domain.model.User;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse authenticate(LoginRequest request) {
        var user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Senha incorreta");
        }

        var token = jwtUtil.generate(user.getUsername(), user.getRole());
        return new LoginResponse(token);
    }

    public User register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEnabled(true);

        return userRepository.save(user);
    }
}


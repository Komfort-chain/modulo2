package com.cabos.login_service.presentation.controller;

import com.cabos.login_service.application.dto.LoginRequest;
import com.cabos.login_service.application.dto.LoginResponse;
import com.cabos.login_service.application.dto.RegisterRequest;
import com.cabos.login_service.application.service.AuthenticationService;
import com.cabos.login_service.domain.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }
}

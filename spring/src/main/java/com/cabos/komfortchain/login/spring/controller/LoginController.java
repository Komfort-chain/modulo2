package com.cabos.komfortchain.login.spring.controller;

import com.cabos.komfortchain.login.domain.exception.InvalidCredentialsException;
import com.cabos.komfortchain.login.domain.exception.UserAlreadyExistsException;
import com.cabos.komfortchain.login.domain.usecase.AuthenticateUserUseCase;
import com.cabos.komfortchain.login.domain.usecase.RegisterUserUseCase;
import com.cabos.komfortchain.login.spring.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class LoginController {

    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;

    public LoginController(RegisterUserUseCase registerUserUseCase,
                           AuthenticateUserUseCase authenticateUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.authenticateUserUseCase = authenticateUserUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        var cmd = new RegisterUserUseCase.RegisterUserCommand(
                request.name(),
                request.email(),
                request.password()
        );
        var user = registerUserUseCase.register(cmd);
        var response = new UserResponseDTO(user.id(), user.name(), user.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        var result = authenticateUserUseCase.authenticate(request.email(), request.password());
        var response = new LoginResponseDTO(
                result.token(),
                "Bearer",
                result.expiresAtEpochSeconds()
        );
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserExists(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}

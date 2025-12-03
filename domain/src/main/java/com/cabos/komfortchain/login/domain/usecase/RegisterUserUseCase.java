package com.cabos.komfortchain.login.domain.usecase;

import com.cabos.komfortchain.login.domain.exception.UserAlreadyExistsException;
import com.cabos.komfortchain.login.domain.model.Role;
import com.cabos.komfortchain.login.domain.model.User;
import com.cabos.komfortchain.login.domain.port.PasswordEncoderPort;
import com.cabos.komfortchain.login.domain.port.UserRepositoryPort;

import java.util.Set;
import java.util.UUID;

public class RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public RegisterUserUseCase(UserRepositoryPort userRepository,
                               PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException(command.email());
        }

        String id = UUID.randomUUID().toString();
        String passwordHash = passwordEncoder.encode(command.rawPassword());

        User user = new User(
                id,
                command.name(),
                command.email(),
                passwordHash,
                Set.of(Role.USER),
                true
        );

        return userRepository.save(user);
    }

    public record RegisterUserCommand(String name, String email, String rawPassword) {
    }
}

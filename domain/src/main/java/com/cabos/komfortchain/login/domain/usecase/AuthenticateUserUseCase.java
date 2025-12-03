package com.cabos.komfortchain.login.domain.usecase;

import com.cabos.komfortchain.login.domain.exception.InvalidCredentialsException;
import com.cabos.komfortchain.login.domain.model.User;
import com.cabos.komfortchain.login.domain.port.PasswordEncoderPort;
import com.cabos.komfortchain.login.domain.port.TokenProviderPort;
import com.cabos.komfortchain.login.domain.port.UserRepositoryPort;

public class AuthenticateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;

    public AuthenticateUserUseCase(UserRepositoryPort userRepository,
                                   PasswordEncoderPort passwordEncoder,
                                   TokenProviderPort tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public AuthenticationResult authenticate(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.active() || !passwordEncoder.matches(rawPassword, user.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = tokenProvider.generateToken(user.id(), user.roles());
        long expiresAt = tokenProvider.getExpiration(token);

        return new AuthenticationResult(token, expiresAt, user);
    }

    public record AuthenticationResult(String token, long expiresAtEpochSeconds, User user) {
    }
}

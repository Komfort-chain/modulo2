package com.cabos.komfortchain.login.spring.configuration;

import com.cabos.komfortchain.login.domain.port.PasswordEncoderPort;
import com.cabos.komfortchain.login.domain.port.TokenProviderPort;
import com.cabos.komfortchain.login.domain.port.UserRepositoryPort;
import com.cabos.komfortchain.login.domain.usecase.AuthenticateUserUseCase;
import com.cabos.komfortchain.login.domain.usecase.RegisterUserUseCase;
import com.cabos.komfortchain.login.domain.usecase.ValidateTokenUseCase;
import com.cabos.komfortchain.login.spring.persistence.adapter.UserRepositoryAdapter;
import com.cabos.komfortchain.login.spring.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfig {

    @Bean
    public UserRepositoryPort userRepositoryPort(UserRepositoryAdapter adapter) {
        return adapter;
    }

    @Bean
    public PasswordEncoderPort passwordEncoderPort(PasswordEncoder passwordEncoder) {
        return new PasswordEncoderPort() {
            @Override
            public String encode(String rawPassword) {
                return passwordEncoder.encode(rawPassword);
            }

            @Override
            public boolean matches(String rawPassword, String encodedPassword) {
                return passwordEncoder.matches(rawPassword, encodedPassword);
            }
        };
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepositoryPort userRepositoryPort,
                                                   PasswordEncoderPort passwordEncoderPort) {
        return new RegisterUserUseCase(userRepositoryPort, passwordEncoderPort);
    }

    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase(UserRepositoryPort userRepositoryPort,
                                                           PasswordEncoderPort passwordEncoderPort,
                                                           TokenProviderPort tokenProviderPort) {
        return new AuthenticateUserUseCase(userRepositoryPort, passwordEncoderPort, tokenProviderPort);
    }

    @Bean
    public ValidateTokenUseCase validateTokenUseCase(TokenProviderPort tokenProviderPort) {
        return new ValidateTokenUseCase(tokenProviderPort);
    }
}

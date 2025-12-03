package com.cabos.komfortchain.login.domain.usecase;

import com.cabos.komfortchain.login.domain.exception.InvalidTokenException;
import com.cabos.komfortchain.login.domain.port.TokenProviderPort;

public class ValidateTokenUseCase {

    private final TokenProviderPort tokenProvider;

    public ValidateTokenUseCase(TokenProviderPort tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public String validate(String token) {
        if (!tokenProvider.validateToken(token)) {
            throw new InvalidTokenException();
        }
        return tokenProvider.getSubject(token);
    }
}

package com.cabos.login_service.infrastructure.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Test
    void deveGerarETestarTokenValido() {
        String username = "testuser";
        String role = "ROLE_ADMIN";

        String token = jwtUtil.generate(username, role);
        String validatedUser = jwtUtil.validate(token);

        Assertions.assertNotNull(token, "O token não deve ser nulo");
        Assertions.assertEquals(username, validatedUser, "O usuário validado deve ser o mesmo");
    }

    @Test
    void deveLancarExcecaoParaTokenExpirado() throws InterruptedException {
        // Gera token já expirado (-1 segundo)
        String tokenExpirado = jwtUtil.generateWithCustomExpiration("expiredUser", "ROLE_USER", -1000);

        // Garante expiração efetiva
        Thread.sleep(50);

        Assertions.assertThrows(
                ExpiredJwtException.class,
                () -> jwtUtil.validate(tokenExpirado),
                "Deve lançar ExpiredJwtException para tokens expirados"
        );
    }

    @Test
    void deveGerarTokensDiferentesParaUsuariosDistintos() {
        String token1 = jwtUtil.generate("userA", "ROLE_USER");
        String token2 = jwtUtil.generate("userB", "ROLE_USER");

        Assertions.assertNotEquals(token1, token2, "Tokens de usuários diferentes não devem ser iguais");
    }
}

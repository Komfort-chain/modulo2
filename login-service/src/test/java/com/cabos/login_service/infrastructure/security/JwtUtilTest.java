package com.cabos.login_service.infrastructure.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Date;

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
    void deveLancarExcecaoParaTokenExpirado() throws Exception {
        // Acessa o campo privado 'expirationMs' e reduz o tempo para 1 ms
        Field field = JwtUtil.class.getDeclaredField("expirationMs");
        field.setAccessible(true);
        field.set(jwtUtil, 1L);

        String token = jwtUtil.generate("expiredUser", "ROLE_USER");

        // Aguarda o token expirar
        Thread.sleep(10);

        Assertions.assertThrows(
                ExpiredJwtException.class,
                () -> jwtUtil.validate(token),
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

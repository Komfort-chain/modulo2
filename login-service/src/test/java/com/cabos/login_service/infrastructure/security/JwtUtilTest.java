package com.cabos.login_service.infrastructure.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Test
    void deveGerarEValidarToken() {
        String token = jwtUtil.generate("alan", "USER");
        String username = jwtUtil.validate(token);

        assertEquals("alan", username);
    }

    @Test
    void deveGerarTokenComExpiracaoCustomizada() {
        String token = jwtUtil.generateWithCustomExpiration("alan", "USER", 2000);
        String username = jwtUtil.validate(token);

        assertEquals("alan", username);
    }
}

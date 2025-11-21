package com.cabos.login_service.application.service;

import com.cabos.login_service.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenServiceTest {

    @Test
    void deveValidarToken() {
        JwtUtil util = mock(JwtUtil.class);
        when(util.validate("abc")).thenReturn("alan");

        TokenService service = new TokenService(util);
        String result = service.validate("abc");

        assertEquals("alan", result);
    }
}

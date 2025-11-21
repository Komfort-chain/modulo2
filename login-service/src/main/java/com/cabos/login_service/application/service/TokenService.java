package com.cabos.login_service.application.service;

import com.cabos.login_service.infrastructure.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final JwtUtil jwtUtil;

    public TokenService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String validate(String token) {
        return jwtUtil.validate(token);
    }
}

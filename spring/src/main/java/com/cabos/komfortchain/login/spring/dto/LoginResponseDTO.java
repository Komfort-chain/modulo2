package com.cabos.komfortchain.login.spring.dto;

public record LoginResponseDTO(
        String accessToken,
        String tokenType,
        long expiresAt
) {
}

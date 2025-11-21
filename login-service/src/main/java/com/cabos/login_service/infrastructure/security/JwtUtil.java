package com.cabos.login_service.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key secret = Keys.hmacShaKeyFor(
            System.getenv().getOrDefault(
                    "JWT_SECRET",
                    "default-school-secret-32-chars-min"
            ).getBytes(StandardCharsets.UTF_8)
    );

    private final long expirationMs = 3600000; // 1h

    public String generate(String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secret, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateWithCustomExpiration(String username, String role, long duration) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + duration);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secret, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validate(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}

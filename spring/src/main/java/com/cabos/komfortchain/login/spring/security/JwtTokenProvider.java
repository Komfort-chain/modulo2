package com.cabos.komfortchain.login.spring.security;

import com.cabos.komfortchain.login.domain.model.Role;
import com.cabos.komfortchain.login.domain.port.TokenProviderPort;
import com.cabos.komfortchain.login.spring.configuration.JwtProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider implements TokenProviderPort {

    private final JwtProperties jwtProperties;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public String generateToken(String subject, Set<Role> roles) {
        long now = Instant.now().getEpochSecond();
        long expiration = now + jwtProperties.getExpirationSeconds();

        String rolesStr = roles.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));

        String payload = subject + "|" + expiration + "|" + rolesStr + "|" + jwtProperties.getSecret();
        return Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean validateToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = decoded.split("\\|");
            if (parts.length < 4) {
                return false;
            }
            long expiration = Long.parseLong(parts[1]);
            String secret = parts[3];
            long now = Instant.now().getEpochSecond();
            return now <= expiration && jwtProperties.getSecret().equals(secret);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getSubject(String token) {
        String decoded = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
        String[] parts = decoded.split("\\|");
        return parts[0];
    }

    @Override
    public long getExpiration(String token) {
        String decoded = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
        String[] parts = decoded.split("\\|");
        return Long.parseLong(parts[1]);
    }
}

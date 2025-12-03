package com.cabos.komfortchain.login.domain.port;

import java.util.Set;

import com.cabos.komfortchain.login.domain.model.Role;

public interface TokenProviderPort {

    String generateToken(String subject, Set<Role> roles);

    boolean validateToken(String token);

    String getSubject(String token);

    long getExpiration(String token);
}
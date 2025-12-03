package com.cabos.komfortchain.login.domain.model;

import java.util.Set;

public record User(
        String id,
        String name,
        String email,
        String passwordHash,
        Set<Role> roles,
        boolean active
) {
}

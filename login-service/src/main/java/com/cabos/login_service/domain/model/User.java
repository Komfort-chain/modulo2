package com.cabos.login_service.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public record User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id,
        String username,
        String password,
        String role,
        boolean enabled
) {}

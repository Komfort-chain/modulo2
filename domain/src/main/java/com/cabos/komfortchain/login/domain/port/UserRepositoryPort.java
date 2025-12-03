package com.cabos.komfortchain.login.domain.port;

import com.cabos.komfortchain.login.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User save(User user);
}
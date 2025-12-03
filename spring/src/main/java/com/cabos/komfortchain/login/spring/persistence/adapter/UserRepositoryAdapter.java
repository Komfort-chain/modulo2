package com.cabos.komfortchain.login.spring.persistence.adapter;

import com.cabos.komfortchain.login.domain.model.User;
import com.cabos.komfortchain.login.domain.port.UserRepositoryPort;
import com.cabos.komfortchain.login.spring.mapper.UserMapper;
import com.cabos.komfortchain.login.spring.persistence.entity.UserEntity;
import com.cabos.komfortchain.login.spring.persistence.repository.SpringDataUserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository repository;
    private final UserMapper userMapper;

    public UserRepositoryAdapter(SpringDataUserRepository repository, UserMapper userMapper) {
        this.repository = repository;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(userMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        UserEntity saved = repository.save(entity);
        return userMapper.toDomain(saved);
    }
}

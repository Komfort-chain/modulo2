package com.cabos.komfortchain.login.spring.mapper;

import com.cabos.komfortchain.login.domain.model.User;
import com.cabos.komfortchain.login.spring.dto.UserResponseDTO;
import com.cabos.komfortchain.login.spring.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(User user) {
        if (user == null) return null;
        UserEntity entity = new UserEntity();
        entity.setId(user.id());
        entity.setName(user.name());
        entity.setEmail(user.email());
        entity.setPasswordHash(user.passwordHash());
        entity.setRoles(user.roles());
        entity.setActive(user.active());
        return entity;
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;
        return new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getRoles(),
                entity.isActive()
        );
    }

    public UserResponseDTO toResponseDTO(User user) {
        if (user == null) return null;
        return new UserResponseDTO(user.id(), user.name(), user.email());
    }
}

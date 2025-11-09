package com.cabos.login_service.infrastructure.persistence;

import com.cabos.login_service.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends UserRepository {
}

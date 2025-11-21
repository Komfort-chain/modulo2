package com.cabos.login_service.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private SecurityFilterChain filterChain;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveCarregarSecurityFilterChain() {
        assertThat(filterChain).isNotNull();
    }

    @Test
    void deveCarregarPasswordEncoder() {
        assertThat(passwordEncoder).isNotNull();
    }
}

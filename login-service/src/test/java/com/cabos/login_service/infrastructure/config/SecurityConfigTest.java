package com.cabos.login_service.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest
@Import(SecurityConfig.class)
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

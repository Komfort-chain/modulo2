package com.cabos.login_service.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    void deveCarregarSecurityFilterChain() throws Exception {
        HttpSecurity http = mock(HttpSecurity.class);
        SecurityFilterChain chain = securityConfig.filterChain(http);
        assertThat(chain).isNotNull();
    }

    @Test
    void deveCarregarPasswordEncoder() {
        assertThat(securityConfig.passwordEncoder()).isNotNull();
    }
}

package com.cabos.login_service.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    private final SecurityConfig config = new SecurityConfig();

    @Test
    void deveCriarSecurityFilterChain() throws Exception {
        HttpSecurity http = mock(HttpSecurity.class);

        SecurityFilterChain chain = config.filterChain(http);

        assertThat(chain).isNotNull();
    }

    @Test
    void deveCriarPasswordEncoder() {
        assertThat(config.passwordEncoder()).isNotNull();
    }
}

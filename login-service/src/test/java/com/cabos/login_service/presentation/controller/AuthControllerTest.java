package com.cabos.login_service.presentation.controller;

import com.cabos.login_service.application.dto.LoginRequest;
import com.cabos.login_service.application.dto.LoginResponse;
import com.cabos.login_service.application.dto.RegisterRequest;
import com.cabos.login_service.application.service.AuthenticationService;
import com.cabos.login_service.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    void deveLogar() throws Exception {
        LoginResponse response = new LoginResponse("token123");
        when(authenticationService.authenticate(any(LoginRequest.class))).thenReturn(response);

        mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"teste\",\"password\":\"123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deveRegistrar() throws Exception {
        User user = new User();
        user.setUsername("novo");
        user.setRole("USER");
        user.setEnabled(true);

        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(user);

        mvc.perform(post("/login/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"novo\",\"password\":\"123\",\"role\":\"USER\"}"))
                .andExpect(status().isOk());
    }
}

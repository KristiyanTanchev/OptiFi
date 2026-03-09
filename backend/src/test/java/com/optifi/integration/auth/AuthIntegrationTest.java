package com.optifi.integration.auth;

import com.optifi.integration.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthIntegrationTest extends AbstractIntegrationTest {

    @Test
    void login_Should_returnUnauthorized_When_credentialsAreInvalid() throws Exception {
        String requestBody = """
                {
                  "username": "testuser",
                  "password": "testpass"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_Should_returnCreated_When_credentialsAreValid() throws Exception {
        String requestBody = """
                {
                    "username": "admin_register",
                    "password": "12345678",
                    "email": "asd42142331232@asd.asd"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    void login_Should_returnOk_When_credentialsAreValid() throws Exception {
        String loginRequestBody = """
                {
                    "username": "admin_login",
                    "password": "12345678",
                    "email": "asd@asd.asd"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isCreated());

        String registerRequestBody = """
                {
                    "username": "admin_login",
                    "password": "12345678"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestBody))
                .andExpect(status().isOk());
    }
}
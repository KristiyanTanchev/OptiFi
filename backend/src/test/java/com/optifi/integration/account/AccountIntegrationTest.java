package com.optifi.integration.account;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optifi.domain.auth.application.AuthService;
import com.optifi.domain.auth.application.command.RegisterUserCommand;
import com.optifi.integration.support.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AccountIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;

    @BeforeEach
    void setup() throws Exception {
        String adminUsername = "admin_login_account";
        String adminPassword = "12345678";
        String adminEmail = "admin_login_account@asd.asd";
        authService.register(
                new RegisterUserCommand(
                        adminUsername,
                        adminPassword,
                        adminEmail
                )
        );
        String loginRequestBody = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, adminUsername, adminPassword);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        adminToken = jsonNode.get("token").asText();
    }

    @Test
    void createAccount_Should_persistAccount() throws Exception {
        String requestBody = """
                {
                    "name": "Account 1",
                    "type": "cash",
                    "currency": "usd"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/accounts")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Account 1"))
                .andExpect(jsonPath("$.type").value("cash"))
                .andExpect(jsonPath("$.currency").value("usd"))
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        long accountId = jsonNode.get("id").asLong();
        mockMvc.perform(get("/api/accounts/" + accountId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId));
    }

    @Test
    void listAccounts_Should_returnOwnedAccounts() throws Exception {
        String requestBody1 = """
                {
                    "name": "Account 1",
                    "type": "cash",
                    "currency": "usd"
                }
                """;
        String requestBody2 = """
                {
                    "name": "Account 2",
                    "type": "cash",
                    "currency": "usd"
                }
                """;
        mockMvc.perform(post("/api/accounts")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody1))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/accounts")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody2))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(get("/api/accounts")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        assertEquals(2, jsonNode.size());
    }

    @Test
    void updateAccount_Should_modifyAccount() throws Exception {
        String requestBodyCreate = """
                {
                    "name": "Account 1",
                    "type": "cash",
                    "currency": "usd"
                }
                """;
        String requestBodyUpdate = """
                {
                    "name": "Account 1 updated",
                    "type": "bank",
                    "currency": "eur"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/accounts")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyCreate))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        long accountId = jsonNode.get("id").asLong();

        mockMvc.perform(put("/api/accounts/" + accountId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyUpdate))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/accounts/" + accountId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Account 1 updated"))
                .andExpect(jsonPath("$.type").value("bank"))
                .andExpect(jsonPath("$.currency").value("eur"));
    }

    @Test
    void archiveAccount_Should_archiveAccount() throws Exception {
        String requestBodyCreate = """
                {
                    "name": "Account 1",
                    "type": "cash",
                    "currency": "usd"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/accounts")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyCreate)
                ).andExpect(status().isCreated())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        long accountId = jsonNode.get("id").asLong();

        mockMvc.perform(put("/api/accounts/" + accountId + "/archive")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/accounts/" + accountId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.archived").value(true));
    }

    @Test
    void deleteAccount_Should_removeAccount() throws Exception {
        String requestBodyCreate = """
                {
                    "name": "Account 1",
                    "type": "cash",
                    "currency": "usd"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/accounts")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyCreate))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        long accountId = jsonNode.get("id").asLong();

        mockMvc.perform(get("/api/accounts/" + accountId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/accounts/" + accountId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());


        mockMvc.perform(get("/api/accounts/" + accountId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }
}

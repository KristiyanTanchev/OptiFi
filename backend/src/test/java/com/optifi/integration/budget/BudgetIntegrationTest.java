package com.optifi.integration.budget;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BudgetIntegrationTest extends AbstractIntegrationTest {
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
    void createBudget_Should_persistBudget() throws Exception {
        String createBudgetRequest = """
                {
                    "name": "Budget 1",
                    "budgetPeriod": "week",
                    "period": "week",
                    "amount": 200,
                    "currency": "eur",
                    "startDate": "2026-02-02",
                    "endDate": "2026-02-08"
                }
                """;
        MvcResult result = mockMvc.perform(post("/api/budgets")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBudgetRequest))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        assertEquals("Budget 1", jsonNode.get("name").asText());
        assertEquals("week", jsonNode.get("period").asText());
        assertEquals("2026-02-02", jsonNode.get("startDate").asText());
        assertEquals("2026-02-08", jsonNode.get("endDate").asText());
        assertEquals(200, jsonNode.get("amount").asLong());
        assertEquals("eur", jsonNode.get("currency").asText());
    }

    @Test
    void getBudgets_Should_returnAllBudgets() throws Exception {
        String createBudgetRequest1 = """
                {
                    "name": "Budget 1",
                    "budgetPeriod": "week",
                    "period": "week",
                    "amount": 200,
                    "currency": "eur",
                    "startDate": "2026-02-02",
                    "endDate": "2026-02-08"
                }
                """;
        String createBudgetRequest2 = """
                {
                    "name": "Budget 2",
                    "budgetPeriod": "week",
                    "period": "week",
                    "amount": 200,
                    "currency": "eur",
                    "startDate": "2026-02-02",
                    "endDate": "2026-02-08"
                }
                """;
        mockMvc.perform(get("/api/budgets")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(0));

        mockMvc.perform(post("/api/budgets")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBudgetRequest1))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/budgets")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBudgetRequest2))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/budgets")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    void getBudget_Should_returnBudget() throws Exception {
        String createBudgetRequest = """
                        {
                            "name": "Budget 1",
                            "budgetPeriod": "week",
                            "period": "week",
                            "amount": 200,
                            "currency": "eur",
                            "startDate": "2026-02-02",
                            "endDate": "2026-02-08"
                        }
                """;
        MvcResult result = mockMvc.perform(post("/api/budgets")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBudgetRequest))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        mockMvc.perform(get("/api/budgets/" + jsonNode.get("id").asLong())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(jsonNode.get("id").asLong()))
                .andExpect(jsonPath("$.name").value(jsonNode.get("name").asText()));
    }

    @Test
    void deleteBudget_Should_removeBudget() throws Exception {
        String createBudgetRequest = """
                        {
                            "name": "Budget 1",
                            "budgetPeriod": "week",
                            "period": "week",
                            "amount": 200,
                            "currency": "eur",
                            "startDate": "2026-02-02",
                            "endDate": "2026-02-08"
                        }
                """;
        MvcResult result = mockMvc.perform(post("/api/budgets")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBudgetRequest))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        long id = jsonNode.get("id").asLong();

        mockMvc.perform(get("/api/budgets/" + jsonNode.get("id").asLong())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(jsonNode.get("id").asLong()))
                .andExpect(jsonPath("$.name").value(jsonNode.get("name").asText()));

        mockMvc.perform(delete("/api/budgets/" + id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/budgets/" + jsonNode.get("id").asLong())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void updateBudget_Should_modifyBudget() throws Exception {
        String createBudgetRequest = """
                        {
                            "name": "Budget 1",
                            "budgetPeriod": "week",
                            "period": "week",
                            "amount": 200,
                            "currency": "eur",
                            "startDate": "2026-02-02",
                            "endDate": "2026-02-08"
                        }
                """;
        MvcResult result = mockMvc.perform(post("/api/budgets")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBudgetRequest))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        long id = jsonNode.get("id").asLong();

        String updateBudgetRequest = """
                        {
                            "name": "Budget 1 updated",
                            "budgetPeriod": "week",
                            "period": "week",
                            "amount": 200,
                            "currency": "eur",
                            "startDate": "2026-02-02",
                            "endDate": "2026-02-08"
                        }
                """;

        mockMvc.perform(put("/api/budgets/" + id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBudgetRequest))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/budgets/" + id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Budget 1 updated"));
    }

    @Test
    void archiveBudget_Should_archiveBudget() throws Exception {
        String createBudgetRequest = """
                                {
                                    "name": "Budget 1",
                                    "budgetPeriod": "week",
                                    "period": "week",
                                    "amount": 200,
                                    "currency": "eur",
                                    "startDate": "2026-02-02",
                                    "endDate": "2026-02-08"
                                }
                """;
        MvcResult result = mockMvc.perform(post("/api/budgets")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBudgetRequest))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        long id = jsonNode.get("id").asLong();

        mockMvc.perform(get("/api/budgets/" + id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.archived").value(false));

        mockMvc.perform(put("/api/budgets/" + id + "/archive")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/budgets/" + id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.archived").value(true));
    }
}

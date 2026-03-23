package com.optifi.integration.category;

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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CategoryIntegrationTest extends AbstractIntegrationTest {
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
    void createCategory_Should_persistCategory() throws Exception {
        String createCategoryRequest = """
                {
                    "name": "name",
                    "description": "description",
                    "icon": "icon"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCategoryRequest))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        assertEquals("name", jsonNode.get("name").asText());
        assertEquals("description", jsonNode.get("description").asText());
        assertEquals("icon", jsonNode.get("icon").asText());
    }

    @Test
    void getUserOwnCategories_Should_returnAllCategories() throws Exception {
        String createCategoryRequest = """
                {
                    "name": "name",
                    "description": "description",
                    "icon": "icon"
                }
                """;
        String createCategoryRequest2 = """
                {
                    "name": "name 2",
                    "description": "description 2",
                    "icon": "icon 2"
                }
                """;
        MvcResult result = mockMvc.perform(get("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCategoryRequest))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        int countBefore = jsonNode.size();

        for (String request : List.of(createCategoryRequest, createCategoryRequest2)) {
            mockMvc.perform(post("/api/categories")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isCreated());
        }
        mockMvc.perform(get("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCategoryRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(countBefore + 2));
    }

    @Test
    void getCategoryById_Should_returnCategory() throws Exception {
        String createCategoryRequest = """
                {
                    "name": "name",
                    "description": "description",
                    "icon": "icon"
                }
                """;
        MvcResult creationResult = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCategoryRequest))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = creationResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        long id = jsonNode.get("id").asLong();

        mockMvc.perform(get("/api/categories/" + id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    void updateCategory_Should_updateCategory() throws Exception {
        String createCategoryRequest = """
                {
                    "name": "name",
                    "description": "description",
                    "icon": "icon"
                }
                """;

        String updateCategoryRequest = """
                {
                    "name": "name updated",
                    "description": "description updated",
                    "icon": "icon updated"
                }
                """;
        MvcResult creationResult = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCategoryRequest))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = creationResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        long id = jsonNode.get("id").asLong();

        mockMvc.perform(put("/api/categories/" + id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateCategoryRequest))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/categories/" + id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("name updated"))
                .andExpect(jsonPath("$.description").value("description updated"))
                .andExpect(jsonPath("$.icon").value("icon updated"))
                .andReturn();

    }

    @Test
    void deleteCategory_Should_deleteCategory() throws Exception {
        String createCategoryRequest = """
                {
                    "name": "name",
                    "description": "description",
                    "icon": "icon"
                }
                """;
        MvcResult creationResult = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCategoryRequest))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = creationResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        long id = jsonNode.get("id").asLong();

        mockMvc.perform(delete("/api/categories/" + id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());


        mockMvc.perform(get("/api/categories/" + id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


}

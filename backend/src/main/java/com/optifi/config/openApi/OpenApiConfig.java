package com.optifi.config.openApi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI optifiOpenApi() {

        return new OpenAPI()
                .info(new Info()
                        .title("OptiFi API")
                        .version("1.0")
                        .description("OptiFi REST API with JWT authentication"))
                .servers(List.of(
                        new Server().url("https://api.optifi.kvtmail.com").description("Live demo"),
                        new Server().url("http://localhost:8080").description("Local")
                ))
                .tags(List.of(
                        new Tag().name("Auth").description("Authentication"),
                        new Tag().name("Users").description("User management"),
                        new Tag().name("Accounts").description("Account management"),
                        new Tag().name("Transactions").description("Transactions management"),
                        new Tag().name("Categories").description("Category management"),
                        new Tag().name("Budgets").description("Budget management"),
                        new Tag().name("Reports").description("Complex reports endpoints"),
                        new Tag().name("FeatureFlags").description("Flags for features under development")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
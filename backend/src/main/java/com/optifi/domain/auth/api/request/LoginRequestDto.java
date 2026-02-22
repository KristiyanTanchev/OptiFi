package com.optifi.domain.auth.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @Schema(description = "Username", example = "kristiyan")
        @NotBlank(message = "Username is required")
        String username,

        @Schema(description = "Password", example = "P@ssw0rd123")
        @NotBlank(message = "Password is required")
        String password
) {
}


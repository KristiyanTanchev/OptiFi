package com.optifi.domain.auth.api.response;

import com.optifi.domain.shared.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record LoginResponseDto(
        @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,
        @Schema(description = "Token type", example = "Bearer")
        String type,
        @Schema(description = "User id", example = "5")
        Long id,
        @Schema(description = "Username", example = "kristiyan")
        String username,
        @Schema(description = "Role", example = "Admin")
        Role role
) {
}
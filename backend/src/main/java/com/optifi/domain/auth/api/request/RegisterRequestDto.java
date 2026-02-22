package com.optifi.domain.auth.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(

        @Schema(description = "Username", example = "kristiyan")
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters")
        String username,

        @Schema(description = "Password", example = "P@ssw0rd123")
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, max = 32, message = "Password must be between 6 and 32 characters")
        String password,

        @Schema(description = "Password", example = "P@ssw0rd123")
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email format is incorrect")
        String email
) {
}

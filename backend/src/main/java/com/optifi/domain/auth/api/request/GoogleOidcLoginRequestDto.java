package com.optifi.domain.auth.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record GoogleOidcLoginRequestDto(
        @Schema(description = "Google id token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..")
        @NotBlank String idToken
) {
}

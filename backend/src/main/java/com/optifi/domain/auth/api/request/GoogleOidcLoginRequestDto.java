package com.optifi.domain.auth.api.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleOidcLoginRequestDto(
        @NotBlank String idToken
) {
}

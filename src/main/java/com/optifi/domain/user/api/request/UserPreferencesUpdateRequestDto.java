package com.optifi.domain.user.api.request;

import jakarta.validation.constraints.NotBlank;

public record UserPreferencesUpdateRequestDto(
        @NotBlank(message = "Currency cannot be blank")
        String currency,

        @NotBlank(message = "Locale cannot be blank")
        String locale
) {
}

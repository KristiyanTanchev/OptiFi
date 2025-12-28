package com.optifi.dto.userDtos;

import jakarta.validation.constraints.NotBlank;

public record UserPreferencesUpdateRequestDto(
        @NotBlank(message = "Currency cannot be blank")
        String currency,

        @NotBlank(message = "Locale cannot be blank")
        String locale
) {
}

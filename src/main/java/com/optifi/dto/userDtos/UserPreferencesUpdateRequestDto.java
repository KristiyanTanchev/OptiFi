package com.optifi.dto.userDtos;

import com.optifi.models.Currency;
import com.optifi.models.SupportedLocale;
import jakarta.validation.constraints.NotBlank;

public record UserPreferencesUpdateRequestDto(
        @NotBlank(message = "Currency cannot be blank")
        Currency currency,

        @NotBlank(message = "Locale cannot be blank")
        SupportedLocale locale
) {
}

package com.optifi.domain.user.api.request;

import com.optifi.domain.shared.Currency;
import com.optifi.domain.shared.SupportedLocale;
import jakarta.validation.constraints.NotBlank;

public record UserPreferencesUpdateRequestDto(
        @NotBlank(message = "Currency cannot be blank")
        Currency currency,

        @NotBlank(message = "Locale cannot be blank")
        SupportedLocale locale
) {
}

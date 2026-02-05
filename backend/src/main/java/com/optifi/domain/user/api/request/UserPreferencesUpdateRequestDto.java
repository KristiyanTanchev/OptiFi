package com.optifi.domain.user.api.request;

import com.optifi.domain.shared.Currency;
import com.optifi.domain.shared.SupportedLocale;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserPreferencesUpdateRequestDto(
        @NotNull(message = "Currency cannot be blank")
        Currency currency,

        @NotNull(message = "Locale cannot be blank")
        SupportedLocale locale,

        @NotNull @NotBlank(message = "Time zone cannot be blank")
        String timezone
) {
}

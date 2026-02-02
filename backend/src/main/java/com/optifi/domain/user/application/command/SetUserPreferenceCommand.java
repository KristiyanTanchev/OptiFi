package com.optifi.domain.user.application.command;

import com.optifi.domain.shared.Currency;
import com.optifi.domain.shared.SupportedLocale;

public record SetUserPreferenceCommand(
        Long userId,
        Currency baseCurrency,
        SupportedLocale locale
) {
}

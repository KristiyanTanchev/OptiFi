package com.optifi.domain.user.application.command;

import com.optifi.domain.shared.model.Currency;
import com.optifi.domain.user.model.SupportedLocale;

public record SetUserPreferenceCommand(Long userId, Currency baseCurrency, SupportedLocale locale) {

    public static SetUserPreferenceCommand from(Long userId, String currency, String locale) {
        return new SetUserPreferenceCommand(
                userId,
                Currency.fromString(currency),
                SupportedLocale.fromString(locale));
    }
}

package com.optifi.services.commands;

import com.optifi.dto.userDtos.UserPreferencesUpdateRequestDto;
import com.optifi.models.Currency;
import com.optifi.models.SupportedLocale;

public record SetUserPreferenceCommand(Long userId, Currency baseCurrency, SupportedLocale locale) {

    public static SetUserPreferenceCommand fromDto(UserPreferencesUpdateRequestDto dto, Long userId) {
        return new SetUserPreferenceCommand(
                userId,
                Currency.fromString(dto.currency()),
                SupportedLocale.fromString(dto.locale()));
    }
}

package com.optifi.domain.user.api.response;

import com.optifi.domain.shared.Currency;
import com.optifi.domain.shared.Role;
import com.optifi.domain.shared.SupportedLocale;
import com.optifi.domain.user.application.result.UserDetailsResult;

public record UserDetailsResponseDto(
        long id,
        String username,
        String email,
        Role role,
        Currency currency,
        SupportedLocale locale
) {
    public static UserDetailsResponseDto fromResult(UserDetailsResult result) {
        return new UserDetailsResponseDto(
                result.id(),
                result.username(),
                result.email(),
                result.role(),
                result.baseCurrency(),
                result.locale()
        );
    }
}

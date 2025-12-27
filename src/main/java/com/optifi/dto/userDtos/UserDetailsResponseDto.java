package com.optifi.dto.userDtos;

import com.optifi.models.Currency;
import com.optifi.services.results.UserDetailsResult;

public record UserDetailsResponseDto(
        long id,
        String username,
        String email,
        Currency currency,
        String locale
) {
    public static UserDetailsResponseDto fromResult(UserDetailsResult result) {
        return new UserDetailsResponseDto(
                result.id(),
                result.username(),
                result.email(),
                result.baseCurrency(),
                result.locale()
        );
    }
}

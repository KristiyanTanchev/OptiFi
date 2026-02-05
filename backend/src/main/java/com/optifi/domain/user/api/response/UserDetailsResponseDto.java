package com.optifi.domain.user.api.response;

import com.optifi.domain.shared.Currency;
import com.optifi.domain.shared.Role;
import com.optifi.domain.shared.SupportedLocale;
import lombok.Builder;

@Builder
public record UserDetailsResponseDto(
        long id,
        String username,
        String email,
        Role role,
        Currency currency,
        SupportedLocale locale,
        String timezone
) {
}

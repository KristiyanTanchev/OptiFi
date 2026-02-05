package com.optifi.domain.account.api.response;

import com.optifi.domain.shared.AccountType;
import com.optifi.domain.shared.Currency;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record AccountDetailsResponseDto(
        long id,
        String name,
        AccountType type,
        Currency currency,
        String institution,
        boolean archived,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

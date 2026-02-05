package com.optifi.domain.account.api.response;

import com.optifi.domain.shared.AccountType;
import com.optifi.domain.shared.Currency;
import lombok.Builder;

@Builder
public record AccountSummaryResponseDto(
        long id,
        String name,
        AccountType type,
        Currency currency,
        String institution,
        boolean archived
) {
}

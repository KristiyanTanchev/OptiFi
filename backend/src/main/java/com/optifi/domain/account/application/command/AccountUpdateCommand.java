package com.optifi.domain.account.application.command;

import com.optifi.domain.shared.AccountType;
import com.optifi.domain.shared.Currency;
import lombok.Builder;

@Builder
public record AccountUpdateCommand(
        long accountId,
        long userId,
        String name,
        AccountType type,
        Currency currency,
        String institution) {
}

package com.optifi.domain.account.application.command;

import com.optifi.domain.account.model.AccountType;
import com.optifi.domain.shared.model.Currency;

public record CreateAccountCommand(
        long userId,
        String name,
        AccountType type,
        Currency currency,
        String institution
) {
    public static CreateAccountCommand from(long userId, String name, String type, String currency, String institution) {
        return new CreateAccountCommand(
                userId,
                name,
                AccountType.fromString(type),
                Currency.fromString(currency),
                institution
        );
    }
}

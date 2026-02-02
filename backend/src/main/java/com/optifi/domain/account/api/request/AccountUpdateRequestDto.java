package com.optifi.domain.account.api.request;

import com.optifi.domain.account.application.command.AccountUpdateCommand;
import com.optifi.domain.shared.AccountType;
import com.optifi.domain.shared.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AccountUpdateRequestDto(
        @NotNull
        @Size(min = 3, max = 32, message = "Account name must be between 3 and 32 characters")
        String name,

        @NotNull
        AccountType type,

        @NotNull
        Currency currency,

        String institution
) {

    public AccountUpdateCommand toUpdateCommand(long userId, long accountId) {
        return new AccountUpdateCommand(accountId, userId, name, type, currency, institution);
    }
}

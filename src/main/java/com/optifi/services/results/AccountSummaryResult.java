package com.optifi.services.results;

import com.optifi.models.Account;
import com.optifi.models.AccountType;
import com.optifi.models.Currency;

public record AccountSummaryResult(
        long id,
        String name,
        AccountType type,
        Currency currency,
        String institution,
        boolean archived
) {
    public static AccountSummaryResult fromEntity(Account account) {
        return new AccountSummaryResult(
                account.getId(),
                account.getName(),
                account.getType(),
                account.getCurrency(),
                account.getInstitution(),
                account.isArchived()
        );
    }
}

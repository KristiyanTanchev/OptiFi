package com.optifi.domain.account.application.result;

import com.optifi.domain.account.model.Account;
import com.optifi.domain.shared.AccountType;
import com.optifi.domain.shared.Currency;

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

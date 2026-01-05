package com.optifi.domain.account.application.result;

import com.optifi.domain.account.model.Account;

public record AccountSummaryResult(
        long id,
        String name,
        String type,
        String currency,
        String institution,
        boolean archived
) {
    public static AccountSummaryResult fromEntity(Account account) {
        return new AccountSummaryResult(
                account.getId(),
                account.getName(),
                account.getType().name(),
                account.getCurrency().name(),
                account.getInstitution(),
                account.isArchived()
        );
    }
}

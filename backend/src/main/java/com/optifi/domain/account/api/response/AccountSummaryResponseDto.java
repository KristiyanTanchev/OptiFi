package com.optifi.domain.account.api.response;

import com.optifi.domain.account.application.result.AccountSummaryResult;
import com.optifi.domain.shared.AccountType;
import com.optifi.domain.shared.Currency;

public record AccountSummaryResponseDto(
        long id,
        String name,
        AccountType type,
        Currency currency,
        String institution,
        boolean archived
) {
    public static AccountSummaryResponseDto fromResult(AccountSummaryResult result) {
        return new AccountSummaryResponseDto(
                result.id(),
                result.name(),
                result.type(),
                result.currency(),
                result.institution(),
                result.archived()
        );
    }
}

package com.optifi.domain.account.api.response;

import com.optifi.domain.account.application.result.AccountSummaryResult;

public record AccountSummaryResponseDto(
        long id,
        String name,
        String type,
        String currency,
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

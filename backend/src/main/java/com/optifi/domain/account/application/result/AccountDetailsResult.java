package com.optifi.domain.account.application.result;

import com.optifi.domain.account.model.Account;
import com.optifi.domain.shared.AccountType;
import com.optifi.domain.shared.Currency;
import com.optifi.domain.transaction.application.result.TransactionSummaryResult;

import java.time.Instant;
import java.util.List;

public record AccountDetailsResult(
        long id,
        String name,
        AccountType type,
        Currency currency,
        String institution,
        List<TransactionSummaryResult> transactions,
        boolean archived,
        Instant createdAt,
        Instant updatedAt
) {
    public static AccountDetailsResult fromEntity(Account account) {
        return new AccountDetailsResult(
                account.getId(),
                account.getName(),
                account.getType(),
                account.getCurrency(),
                account.getInstitution(),
                account.getTransactions()
                        .stream()
                        .map(TransactionSummaryResult::fromEntity)
                        .toList(),
                account.isArchived(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }
}

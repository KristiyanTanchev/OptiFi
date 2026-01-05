package com.optifi.domain.account.application.result;

import com.optifi.domain.account.model.Account;
import com.optifi.domain.transaction.application.result.TransactionSummaryResult;

import java.time.Instant;
import java.util.List;

public record AccountDetailsResult(
        long id,
        String name,
        String type,
        String currency,
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
                account.getType().name(),
                account.getCurrency().name(),
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

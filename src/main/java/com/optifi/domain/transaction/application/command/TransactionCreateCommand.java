package com.optifi.domain.transaction.application.command;

import com.optifi.domain.account.model.Account;
import com.optifi.domain.transaction.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionCreateCommand(
        Long userId,
        Long accountId,
        BigDecimal amount,
        String description,
        Instant occurredAt
) {
    public static TransactionCreateCommand from(
            Long userId,
            Long accountId,
            BigDecimal amount,
            String description,
            String occurredAt) {
        Instant occurredAtInstant = Instant.parse(occurredAt);
        return new TransactionCreateCommand(userId, accountId, amount, description, occurredAtInstant);
    }

    public Transaction toEntity(Account account) {
        return Transaction.builder()
                .account(account)
                .amount(amount)
                .description(description)
                .occurredAt(occurredAt)
                .build();
    }
}

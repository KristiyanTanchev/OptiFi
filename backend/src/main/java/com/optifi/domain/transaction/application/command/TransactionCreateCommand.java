package com.optifi.domain.transaction.application.command;

import com.optifi.domain.account.model.Account;
import com.optifi.domain.category.model.Category;
import com.optifi.domain.transaction.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionCreateCommand(
        Long userId,
        Long accountId,
        BigDecimal amount,
        String description,
        Instant occurredAt,
        Long categoryId
) {
    public Transaction toEntity(Account account, Category category) {
        return Transaction.builder()
                .account(account)
                .amount(amount)
                .description(description)
                .occurredAt(occurredAt)
                .category(category)
                .build();
    }
}

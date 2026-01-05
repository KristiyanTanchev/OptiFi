package com.optifi.domain.transaction.application.result;

import com.optifi.domain.transaction.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionSummaryResult(
        long id,
        long accountId,
        Instant occurredAt,
        BigDecimal amount,
        String description
) {
    public static TransactionSummaryResult fromEntity(Transaction tx) {
        return new TransactionSummaryResult(
                tx.getId(),
                tx.getAccount().getId(),
                tx.getOccurredAt(),
                tx.getAmount(),
                tx.getDescription()
        );
    }
}

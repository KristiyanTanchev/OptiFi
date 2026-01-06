package com.optifi.domain.transaction.application.result;

import com.optifi.domain.transaction.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionDetailsResult(
        long id,
        long accountId,
        Instant occurredAt,
        BigDecimal amount,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
    public static TransactionDetailsResult fromEntity(Transaction tx) {
        return new TransactionDetailsResult(
                tx.getId(),
                tx.getAccount().getId(),
                tx.getOccurredAt(),
                tx.getAmount(),
                tx.getDescription(),
                tx.getCreatedAt(),
                tx.getUpdatedAt()
        );
    }
}

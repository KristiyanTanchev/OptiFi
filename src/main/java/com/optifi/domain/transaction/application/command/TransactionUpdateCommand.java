package com.optifi.domain.transaction.application.command;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionUpdateCommand(
        Long id,
        Long userId,
        BigDecimal amount,
        String description,
        Instant occurredAt
) {
    public static TransactionUpdateCommand from(
            Long id,
            Long userId,
            BigDecimal amount,
            String description,
            String occurredAt) {
        Instant occurredAtInstant = Instant.parse(occurredAt);
        return new TransactionUpdateCommand(id, userId, amount, description, occurredAtInstant);
    }
}

package com.optifi.domain.transaction.application.command;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateTransactionCommand(
        long accountId,
        BigDecimal amount,
        String description,
        Instant occurredAt
) {
}

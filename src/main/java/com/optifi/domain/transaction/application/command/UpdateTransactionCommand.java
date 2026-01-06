package com.optifi.domain.transaction.application.command;

import java.math.BigDecimal;
import java.time.Instant;

public record UpdateTransactionCommand(
        long id,
        long accountId,
        BigDecimal amount,
        String description,
        Instant occurredAt
) {
}

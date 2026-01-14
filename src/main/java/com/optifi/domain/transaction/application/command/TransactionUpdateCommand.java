package com.optifi.domain.transaction.application.command;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionUpdateCommand(
        Long userId,
        Long accountId,
        Long id,
        BigDecimal amount,
        String description,
        Instant occurredAt,
        Long categoryId
) {
}

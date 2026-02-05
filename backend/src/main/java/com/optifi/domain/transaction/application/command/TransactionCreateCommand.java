package com.optifi.domain.transaction.application.command;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record TransactionCreateCommand(
        Long userId,
        Long accountId,
        BigDecimal amount,
        String description,
        Instant occurredAt,
        Long categoryId
) {
}

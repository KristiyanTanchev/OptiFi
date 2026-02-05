package com.optifi.domain.transaction.application.command;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record TransactionQuery(
        Long userId,
        Long accountId,
        Instant from,
        Instant to,
        BigDecimal min,
        BigDecimal max,
        String description
) {
}

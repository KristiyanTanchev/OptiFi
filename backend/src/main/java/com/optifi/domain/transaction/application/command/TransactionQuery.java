package com.optifi.domain.transaction.application.command;

import java.math.BigDecimal;
import java.time.Instant;

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

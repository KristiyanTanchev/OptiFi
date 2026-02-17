package com.optifi.domain.transaction.application.command;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record TransactionQuery(
        Long userId,
        Long accountId,
        List<Long> accountIds,
        List<Long> categoryIds,
        Instant from,
        Instant to,
        BigDecimal min,
        BigDecimal max,
        String description
) {
}

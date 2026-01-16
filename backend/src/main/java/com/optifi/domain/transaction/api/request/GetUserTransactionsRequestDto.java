package com.optifi.domain.transaction.api.request;

import com.optifi.domain.transaction.application.command.TransactionQuery;

import java.math.BigDecimal;
import java.time.Instant;

public record GetUserTransactionsRequestDto(
        Instant startDate,
        Instant endDate,
        BigDecimal min,
        BigDecimal max,
        String description
) {
    public TransactionQuery toQuery(Long userId, Long accountId) {
        return new TransactionQuery(userId, accountId, startDate, endDate, min, max, description);
    }
}

package com.optifi.domain.transaction.application.command;

import java.time.Instant;

public record TransactionGetSummaryCommand(
        Long userId,
        Long accountId,
        Instant from,
        Instant to,
        Long categoryId,
        String query
) {
}

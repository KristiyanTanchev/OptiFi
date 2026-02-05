package com.optifi.domain.transaction.application.command;

import lombok.Builder;

import java.time.Instant;

@Builder
public record TransactionGetSummaryCommand(
        Long userId,
        Long accountId,
        Instant from,
        Instant to,
        Long categoryId,
        String query
) {
}

package com.optifi.domain.transaction.api.request;

import com.optifi.domain.transaction.application.command.TransactionGetSummaryCommand;

import java.time.Instant;

public record TransactionGetSummaryRequestDto(
        Instant from,
        Instant to,
        Long categoryId,
        String query
) {
    public TransactionGetSummaryCommand toCommand(Long userId, Long accountId) {
        return new TransactionGetSummaryCommand(userId, accountId, from, to, categoryId, query);
    }
}

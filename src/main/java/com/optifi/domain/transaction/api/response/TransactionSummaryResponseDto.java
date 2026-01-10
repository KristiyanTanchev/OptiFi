package com.optifi.domain.transaction.api.response;

import com.optifi.domain.transaction.application.result.TransactionSummaryResult;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionSummaryResponseDto(
        long id,
        long accountId,
        Instant occurredAt,
        BigDecimal amount
) {
    public static TransactionSummaryResponseDto fromResult(TransactionSummaryResult result) {
        return new TransactionSummaryResponseDto(
                result.id(),
                result.accountId(),
                result.occurredAt(),
                result.amount()
        );
    }
}

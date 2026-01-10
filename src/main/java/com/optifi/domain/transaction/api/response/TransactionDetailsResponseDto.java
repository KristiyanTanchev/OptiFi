package com.optifi.domain.transaction.api.response;

import com.optifi.domain.transaction.application.result.TransactionDetailsResult;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionDetailsResponseDto(
        long id,
        long accountId,
        Instant occurredAt,
        BigDecimal amount,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
    public static TransactionDetailsResponseDto fromResult(TransactionDetailsResult result) {
        return new TransactionDetailsResponseDto(
                result.id(),
                result.accountId(),
                result.occurredAt(),
                result.amount(),
                result.description(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}

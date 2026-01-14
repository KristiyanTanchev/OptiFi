package com.optifi.domain.transaction.api.response;

import com.optifi.domain.category.api.response.CategorySummaryResponseDto;
import com.optifi.domain.transaction.application.result.TransactionDetailsResult;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionDetailsResponseDto(
        long id,
        long accountId,
        Instant occurredAt,
        BigDecimal amount,
        String description,
        CategorySummaryResponseDto category,
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
                CategorySummaryResponseDto.fromResult(result.category()),
                result.createdAt(),
                result.updatedAt()
        );
    }
}

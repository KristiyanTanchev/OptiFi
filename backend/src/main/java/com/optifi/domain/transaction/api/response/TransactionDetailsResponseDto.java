package com.optifi.domain.transaction.api.response;

import com.optifi.domain.category.api.response.CategorySummaryResponseDto;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
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
}

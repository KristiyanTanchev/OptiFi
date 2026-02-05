package com.optifi.domain.transaction.api.response;

import com.optifi.domain.category.api.response.CategorySummaryResponseDto;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
public record TransactionDetailsResponseDto(
        long id,
        long accountId,
        OffsetDateTime occurredAt,
        BigDecimal amount,
        String description,
        CategorySummaryResponseDto category,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

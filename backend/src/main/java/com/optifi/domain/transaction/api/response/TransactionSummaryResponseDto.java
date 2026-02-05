package com.optifi.domain.transaction.api.response;

import com.optifi.domain.category.api.response.CategorySummaryResponseDto;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
public record TransactionSummaryResponseDto(
        long id,
        long accountId,
        OffsetDateTime occurredAt,
        BigDecimal amount,
        CategorySummaryResponseDto category
) {
}

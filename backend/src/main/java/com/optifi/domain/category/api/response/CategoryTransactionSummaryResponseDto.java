package com.optifi.domain.category.api.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
public record CategoryTransactionSummaryResponseDto(
        long id,
        long accountId,
        OffsetDateTime occurredAt,
        BigDecimal amount
) {
}

package com.optifi.domain.transaction.api.response;

import com.optifi.domain.category.api.response.CategorySummaryResponseDto;
import com.optifi.domain.transaction.application.result.TransactionSummaryResult;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionSummaryResponseDto(
        long id,
        long accountId,
        Instant occurredAt,
        BigDecimal amount,
        CategorySummaryResponseDto category
) {
    public static TransactionSummaryResponseDto fromResult(TransactionSummaryResult result) {
        return new TransactionSummaryResponseDto(
                result.id(),
                result.accountId(),
                result.occurredAt(),
                result.amount(),
                CategorySummaryResponseDto.fromResult(result.categoryName())
        );
    }
}

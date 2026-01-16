package com.optifi.domain.category.application.result;

import com.optifi.domain.category.model.Category;
import com.optifi.domain.transaction.application.result.TransactionSummaryResult;

import java.time.Instant;
import java.util.List;

public record CategoryDetailsResult(
        Long id,
        String name,
        String description,
        String icon,
        List<TransactionSummaryResult> transactions,
        Instant createdAt,
        Instant updatedAt
) {
    public static CategoryDetailsResult fromEntity(Category category) {
        return new CategoryDetailsResult(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getIcon(),
                category.getTransactions()
                        .stream()
                        .map(TransactionSummaryResult::fromEntity)
                        .toList(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}

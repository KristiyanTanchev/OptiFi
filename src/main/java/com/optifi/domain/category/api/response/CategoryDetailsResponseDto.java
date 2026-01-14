package com.optifi.domain.category.api.response;

import com.optifi.domain.category.application.result.CategoryDetailsResult;
import com.optifi.domain.transaction.api.response.TransactionSummaryResponseDto;

import java.time.Instant;
import java.util.List;

public record CategoryDetailsResponseDto(
        Long id,
        String name,
        String description,
        String icon,
        List<TransactionSummaryResponseDto> transactions,
        Instant createdAt,
        Instant updatedAt
) {
    public static CategoryDetailsResponseDto fromResult(CategoryDetailsResult result) {
        return new CategoryDetailsResponseDto(
                result.id(),
                result.name(),
                result.description(),
                result.icon(),
                result.transactions().
                        stream().
                        map(TransactionSummaryResponseDto::fromResult).
                        toList(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}

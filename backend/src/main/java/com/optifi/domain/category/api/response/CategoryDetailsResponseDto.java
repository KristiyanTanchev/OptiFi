package com.optifi.domain.category.api.response;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record CategoryDetailsResponseDto(
        Long id,
        String name,
        String description,
        String icon,
        List<CategoryTransactionSummaryResponseDto> transactions,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        boolean canEdit,
        boolean canDelete
) {
}

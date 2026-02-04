package com.optifi.domain.category.api.response;

import com.optifi.domain.transaction.api.response.TransactionSummaryResponseDto;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record CategoryDetailsResponseDto(
        Long id,
        String name,
        String description,
        String icon,
        List<TransactionSummaryResponseDto> transactions,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        boolean canEdit,
        boolean canDelete
) {
}

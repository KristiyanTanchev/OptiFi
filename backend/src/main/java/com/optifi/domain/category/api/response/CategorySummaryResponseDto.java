package com.optifi.domain.category.api.response;

import lombok.Builder;

@Builder
public record CategorySummaryResponseDto(
        Long id,
        String name,
        String icon,
        boolean canEdit,
        boolean canDelete
) {
}

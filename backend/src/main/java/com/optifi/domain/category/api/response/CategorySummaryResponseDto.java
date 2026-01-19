package com.optifi.domain.category.api.response;

import com.optifi.domain.category.application.result.CategorySummaryResult;

public record CategorySummaryResponseDto(
        Long id,
        String name,
        String icon,
        boolean canEdit,
        boolean canDelete
) {
    public static CategorySummaryResponseDto fromResult(CategorySummaryResult result) {
        return new CategorySummaryResponseDto(
                result.id(),
                result.name(),
                result.icon(),
                result.canEdit(),
                result.canDelete()
        );
    }
}

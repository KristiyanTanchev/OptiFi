package com.optifi.domain.category.application.result;

import com.optifi.domain.category.model.Category;

import java.time.Instant;

public record CategorySummaryResult(
        Long id,
        String name,
        String icon,
        Instant createdAt,
        Instant updatedAt,
        boolean canEdit,
        boolean canDelete
) {
    public static CategorySummaryResult fromEntity(Category category) {
        return new CategorySummaryResult(
                category.getId(),
                category.getName(),
                category.getIcon(),
                category.getCreatedAt(),
                category.getUpdatedAt(),
                !category.isDefault(),
                !category.isDefault()
        );
    }
}

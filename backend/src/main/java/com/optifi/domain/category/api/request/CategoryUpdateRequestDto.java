package com.optifi.domain.category.api.request;

import com.optifi.domain.category.application.command.CategoryUpdateCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryUpdateRequestDto(
        @NotNull @Size(min = 3, max = 100, message = "Category name must be between 3 and 100 characters")
        String name,

        @NotNull @Size(min = 3, max = 255, message = "Category description must be between 3 and 255 characters")
        String description,

        @NotNull @Size(min = 1, max = 255, message = "Category icon must be between 1 and 255 characters")
        String icon
) {
    public CategoryUpdateCommand toUpdateCommand(Long userId, Long categoryId) {
        return new CategoryUpdateCommand(userId, categoryId, name, description, icon);
    }
}

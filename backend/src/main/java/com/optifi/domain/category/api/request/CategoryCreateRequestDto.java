package com.optifi.domain.category.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequestDto(
        @NotNull @Size(message = "Category name must be between 3 and 100 characters", min = 3, max = 100)
        String name,

        @NotNull @Size(message = "Category description must be between 3 and 255 characters", min = 3, max = 255)
        String description,

        @NotNull @Size(message = "Category icon must be between 1 and 255 characters", min = 1, max = 255)
        String icon
) {
}

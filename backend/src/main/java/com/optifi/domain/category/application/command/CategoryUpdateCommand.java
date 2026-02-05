package com.optifi.domain.category.application.command;

import lombok.Builder;

@Builder
public record CategoryUpdateCommand(
        Long userId,
        Long categoryId,
        String name,
        String description,
        String icon
) {
}

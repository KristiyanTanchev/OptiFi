package com.optifi.domain.category.application.command;

public record CategoryUpdateCommand(
        Long userId,
        Long categoryId,
        String name,
        String description,
        String icon
) {
}

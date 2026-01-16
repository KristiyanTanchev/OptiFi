package com.optifi.domain.category.application.command;

public record CategoryCreateCommand(
        Long userId,
        String name,
        String description,
        String icon
) {
}

package com.optifi.domain.category.application.command;

import lombok.Builder;

@Builder
public record CategoryCreateCommand(
        Long userId,
        String name,
        String description,
        String icon
) {
}

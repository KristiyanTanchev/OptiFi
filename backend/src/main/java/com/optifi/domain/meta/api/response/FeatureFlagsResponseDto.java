package com.optifi.domain.meta.api.response;

import lombok.Builder;

@Builder
public record FeatureFlagsResponseDto(
        boolean registrationEnabled,
        boolean createCategoryEnabled
) {
}

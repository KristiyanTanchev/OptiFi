package com.optifi.domain.meta.api.response;

public record FeatureFlagsResponseDto(
        boolean registrationEnabled,
        boolean createCategoryEnabled
) {
}

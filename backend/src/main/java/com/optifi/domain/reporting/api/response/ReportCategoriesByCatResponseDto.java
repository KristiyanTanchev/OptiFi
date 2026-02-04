package com.optifi.domain.reporting.api.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ReportCategoriesByCatResponseDto(
        Long categoryId,
        String categoryName,
        String icon,
        BigDecimal amount,
        BigDecimal percent
) {
}

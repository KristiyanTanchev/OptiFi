package com.optifi.domain.reporting.repository.aggregations;

import java.math.BigDecimal;

public record ReportCategoriesByCatAgg(
        Long categoryId,
        String categoryName,
        String icon,
        BigDecimal amount,
        BigDecimal percent
) {
}

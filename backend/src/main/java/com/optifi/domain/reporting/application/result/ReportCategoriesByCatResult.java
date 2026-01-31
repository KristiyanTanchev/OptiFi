package com.optifi.domain.reporting.application.result;

import java.math.BigDecimal;

public record ReportCategoriesByCatResult(
        Long categoryId,
        String categoryName,
        String icon,
        BigDecimal amount,
        BigDecimal percent
) {
}

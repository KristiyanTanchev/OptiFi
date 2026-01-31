package com.optifi.domain.reporting.application.result;


import java.math.BigDecimal;
import java.util.List;

public record ReportCategoriesResult(
        String currency,
        String type,
        BigDecimal total,
        List<ReportCategoriesByCatResult> items
) {
}

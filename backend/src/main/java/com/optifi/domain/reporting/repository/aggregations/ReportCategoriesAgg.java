package com.optifi.domain.reporting.repository.aggregations;

import java.math.BigDecimal;
import java.util.List;

public record ReportCategoriesAgg(
        String type,
        BigDecimal total,
        List<ReportCategoriesByCatAgg> items
) {
}

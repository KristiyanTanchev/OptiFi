package com.optifi.domain.reporting.application.result;

import com.optifi.domain.reporting.repository.aggregations.ReportCategoriesByCatAgg;

import java.math.BigDecimal;

public record ReportCategoriesByCatResult(
        Long categoryId,
        String categoryName,
        String icon,
        BigDecimal amount,
        BigDecimal percent
) {
    public static ReportCategoriesByCatResult from(ReportCategoriesByCatAgg aggregation, BigDecimal total) {
        return new ReportCategoriesByCatResult(
                aggregation.categoryId(),
                aggregation.categoryName(),
                aggregation.icon(),
                aggregation.amount(),
                total.doubleValue() == 0 ? BigDecimal.ZERO :
                        BigDecimal.valueOf(aggregation.amount().doubleValue() / total.doubleValue() * 100)
        );
    }
}

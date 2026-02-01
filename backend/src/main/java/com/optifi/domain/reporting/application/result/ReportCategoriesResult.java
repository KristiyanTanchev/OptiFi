package com.optifi.domain.reporting.application.result;


import com.optifi.domain.reporting.repository.aggregations.ReportCategoriesAgg;
import com.optifi.domain.reporting.repository.aggregations.ReportCategoriesByCatAgg;
import com.optifi.domain.shared.model.Currency;

import java.math.BigDecimal;
import java.util.List;

public record ReportCategoriesResult(
        String currency,
        String type,
        BigDecimal total,
        List<ReportCategoriesByCatResult> items
) {
    public static ReportCategoriesResult from(
            Currency currency,
            String type,
            ReportCategoriesAgg aggregation,
            List<ReportCategoriesByCatAgg> byCat) {
        return new ReportCategoriesResult(
                currency.name(),
                type,
                aggregation.total(),
                byCat.stream()
                        .map(b -> ReportCategoriesByCatResult.from(b, aggregation.total()))
                        .toList()
        );
    }
}

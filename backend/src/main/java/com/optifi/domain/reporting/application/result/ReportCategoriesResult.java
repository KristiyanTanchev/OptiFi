package com.optifi.domain.reporting.application.result;


import com.optifi.domain.reporting.repository.aggregations.ReportCategoriesAgg;
import com.optifi.domain.reporting.repository.aggregations.ReportCategoriesByCatAgg;
import com.optifi.domain.shared.Currency;
import com.optifi.domain.shared.TransactionType;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ReportCategoriesResult(
        Currency currency,
        TransactionType type,
        BigDecimal total,
        List<ReportCategoriesByCatResult> items
) {
    public static ReportCategoriesResult from(
            Currency currency,
            TransactionType type,
            ReportCategoriesAgg aggregation,
            List<ReportCategoriesByCatAgg> byCat) {
        return ReportCategoriesResult.builder()
                .currency(currency)
                .type(type)
                .total(aggregation == null ? BigDecimal.ZERO : aggregation.total())
                .items(byCat == null || aggregation == null ?
                        List.of() :
                        byCat.stream()
                                .map(b ->
                                        ReportCategoriesByCatResult.from(b, aggregation.total())
                                ).toList())
                .build();
    }
}

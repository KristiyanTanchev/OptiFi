package com.optifi.domain.reporting.repository.aggregations;

import java.math.BigDecimal;

public record ReportSummaryAgg(
        BigDecimal income,
        BigDecimal expense,
        Long count
) {
}

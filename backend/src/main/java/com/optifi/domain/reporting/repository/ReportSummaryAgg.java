package com.optifi.domain.reporting.repository;

import java.math.BigDecimal;

public record ReportSummaryAgg(
        BigDecimal income,
        BigDecimal expense,
        Long count
) {
}

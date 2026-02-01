package com.optifi.domain.reporting.repository.aggregations;

import java.math.BigDecimal;

public record ReportSummaryByAccountAgg(
        Long accountId,
        String accountName,
        BigDecimal income,
        BigDecimal expense,
        Long count
) {
}

package com.optifi.domain.reporting.repository;

import java.math.BigDecimal;

public record ReportSummaryByAccountAgg(
        Long accountId,
        String accountName,
        BigDecimal income,
        BigDecimal expense,
        Long count
) {
}

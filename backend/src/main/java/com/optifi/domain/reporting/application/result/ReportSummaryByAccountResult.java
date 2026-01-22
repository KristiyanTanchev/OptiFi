package com.optifi.domain.reporting.application.result;

import com.optifi.domain.reporting.repository.ReportSummaryByAccountAgg;

import java.math.BigDecimal;

public record ReportSummaryByAccountResult(
        Long accountId,
        String accountName,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal net,
        Long count
) {

    public static ReportSummaryByAccountResult from(
            ReportSummaryByAccountAgg aggregation
    ) {
        return new ReportSummaryByAccountResult(
                aggregation.accountId(),
                aggregation.accountName(),
                aggregation.income(),
                aggregation.expense(),
                aggregation.income().subtract(aggregation.expense()),
                aggregation.count()
        );
    }
}

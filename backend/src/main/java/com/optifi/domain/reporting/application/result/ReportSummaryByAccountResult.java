package com.optifi.domain.reporting.application.result;

import com.optifi.domain.transaction.repository.ReportSummaryByAccountProjection;

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
            ReportSummaryByAccountProjection projection
    ) {
        return new ReportSummaryByAccountResult(
                projection.accountId(),
                projection.accountName(),
                projection.income(),
                projection.expense(),
                projection.income().subtract(projection.expense()),
                projection.count()
        );
    }
}

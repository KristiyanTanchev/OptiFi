package com.optifi.domain.reporting.application.result;

import com.optifi.domain.transaction.repository.ReportSummaryByAccountProjection;
import com.optifi.domain.transaction.repository.ReportSummaryProjection;

import java.math.BigDecimal;
import java.util.List;

public record ReportSummaryResult(
        String currency,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal net,
        Long count,
        List<ReportSummaryByAccountResult> byAccount
) {

    public static ReportSummaryResult from(
            ReportSummaryProjection projection,
            List<ReportSummaryByAccountProjection> byAccount
    ) {
        return new ReportSummaryResult(
                projection.currency().name(),
                projection.income(),
                projection.expense(),
                projection.income().subtract(projection.expense()),
                projection.count(),
                byAccount.stream().map(ReportSummaryByAccountResult::from).toList()
        );
    }
}

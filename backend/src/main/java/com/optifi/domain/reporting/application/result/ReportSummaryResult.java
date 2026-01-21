package com.optifi.domain.reporting.application.result;

import com.optifi.domain.shared.model.Currency;
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
            Currency currency,
            ReportSummaryProjection projection,
            List<ReportSummaryByAccountProjection> byAccount
    ) {
        return new ReportSummaryResult(
                currency.name(),
                projection.getIncome(),
                projection.getExpense(),
                projection.getIncome().subtract(projection.getExpense()),
                projection.getCount(),
                byAccount.stream().map(ReportSummaryByAccountResult::from).toList()
        );
    }
}

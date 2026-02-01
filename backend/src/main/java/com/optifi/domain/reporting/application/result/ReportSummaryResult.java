package com.optifi.domain.reporting.application.result;

import com.optifi.domain.reporting.repository.aggregations.ReportSummaryAgg;
import com.optifi.domain.reporting.repository.aggregations.ReportSummaryByAccountAgg;
import com.optifi.domain.shared.model.Currency;

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
            ReportSummaryAgg aggregation,
            List<ReportSummaryByAccountAgg> byAccount
    ) {
        return new ReportSummaryResult(
                currency.name(),
                aggregation.income(),
                aggregation.expense(),
                aggregation.income().subtract(aggregation.expense()),
                aggregation.count(),
                byAccount.stream().map(ReportSummaryByAccountResult::from).toList()
        );
    }
}

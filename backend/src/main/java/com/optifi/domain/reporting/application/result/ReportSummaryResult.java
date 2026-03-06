package com.optifi.domain.reporting.application.result;

import com.optifi.domain.reporting.repository.aggregations.ReportSummaryAgg;
import com.optifi.domain.reporting.repository.aggregations.ReportSummaryByAccountAgg;
import com.optifi.domain.shared.Currency;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ReportSummaryResult(
        Currency currency,
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
        if (aggregation == null) {
            return ReportSummaryResult.builder()
                    .currency(currency)
                    .count(0L)
                    .byAccount(byAccount == null ?
                            List.of() :
                            byAccount.stream().map(ReportSummaryByAccountResult::from).toList())
                    .build();
        } else {
            return ReportSummaryResult.builder()
                    .currency(currency)
                    .income(aggregation.income())
                    .expense(aggregation.expense())
                    .net(aggregation.income().subtract(aggregation.expense()))
                    .count(aggregation.count())
                    .byAccount(byAccount.stream().map(ReportSummaryByAccountResult::from).toList())
                    .build();
        }
    }
}

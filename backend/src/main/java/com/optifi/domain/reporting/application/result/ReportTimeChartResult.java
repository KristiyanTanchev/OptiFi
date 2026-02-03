package com.optifi.domain.reporting.application.result;

import com.optifi.domain.shared.Currency;
import com.optifi.domain.shared.TimeBucket;
import com.optifi.domain.shared.TransactionType;

import java.util.List;

public record ReportTimeChartResult(
        TimeBucket bucket,
        TransactionType type,
        Currency currency,
        List<ReportTimeChartByPeriodResult> points
) {
}

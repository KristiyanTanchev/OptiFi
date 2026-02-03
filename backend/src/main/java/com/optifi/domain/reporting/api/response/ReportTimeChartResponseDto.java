package com.optifi.domain.reporting.api.response;

import com.optifi.domain.reporting.application.result.ReportTimeChartResult;
import com.optifi.domain.shared.Currency;
import com.optifi.domain.shared.TimeBucket;
import com.optifi.domain.shared.TransactionType;

import java.util.List;

public record ReportTimeChartResponseDto(
        TimeBucket bucket,
        TransactionType type,
        Currency currency,
        List<ReportTimeChartByPeriodResponseDto> points
) {
    public static ReportTimeChartResponseDto from(ReportTimeChartResult result) {
        return new ReportTimeChartResponseDto(
                result.bucket(),
                result.type(),
                result.currency(),
                result.points().stream().map(ReportTimeChartByPeriodResponseDto::from).toList()
        );
    }
}

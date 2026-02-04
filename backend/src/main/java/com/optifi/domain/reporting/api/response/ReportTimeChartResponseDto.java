package com.optifi.domain.reporting.api.response;

import com.optifi.domain.shared.Currency;
import com.optifi.domain.shared.TimeBucket;
import com.optifi.domain.shared.TransactionType;
import lombok.Builder;

import java.util.List;

@Builder
public record ReportTimeChartResponseDto(
        TimeBucket bucket,
        TransactionType type,
        Currency currency,
        List<ReportTimeChartByPeriodResponseDto> points
) {
}

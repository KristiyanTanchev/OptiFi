package com.optifi.domain.reporting.application.command;

import com.optifi.domain.shared.Currency;
import com.optifi.domain.shared.TimeBucket;
import com.optifi.domain.shared.TransactionType;
import lombok.Builder;

import java.time.LocalDate;
import java.time.ZoneId;

@Builder
public record ReportTimeChartCommand(
        Long userId,
        ZoneId zoneId,
        Currency baseCurrency,
        TransactionType type,
        TimeBucket bucket,
        LocalDate from,
        LocalDate to
) {
}

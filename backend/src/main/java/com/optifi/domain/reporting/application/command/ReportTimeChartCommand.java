package com.optifi.domain.reporting.application.command;

import com.optifi.domain.shared.TimeBucket;
import com.optifi.domain.shared.TransactionType;

import java.time.LocalDate;

public record ReportTimeChartCommand(
        Long userId,
        TransactionType type,
        TimeBucket bucket,
        LocalDate from,
        LocalDate to
) {
}

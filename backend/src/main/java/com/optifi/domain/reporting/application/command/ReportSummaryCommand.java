package com.optifi.domain.reporting.application.command;

import com.optifi.domain.shared.Currency;

import java.time.Instant;

public record ReportSummaryCommand(
        Long userId,
        Instant from,
        Instant to,
        Currency currency
) {
}

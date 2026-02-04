package com.optifi.domain.reporting.application.command;

import com.optifi.domain.shared.Currency;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ReportSummaryCommand(
        Long userId,
        Instant from,
        Instant to,
        Currency currency
) {
}

package com.optifi.domain.reporting.application.command;

import com.optifi.domain.shared.model.Currency;

import java.time.Instant;

public record ReportSummaryCommand(
        Long userId,
        Instant from,
        Instant to,
        Currency currency
) {
    public static ReportSummaryCommand from(long userId, Instant from, Instant to, String currency) {
        return new ReportSummaryCommand(userId, from, to, Currency.fromString(currency));
    }
}

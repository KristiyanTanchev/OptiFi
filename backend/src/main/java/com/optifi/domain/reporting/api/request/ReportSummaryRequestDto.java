package com.optifi.domain.reporting.api.request;

import com.optifi.domain.reporting.application.command.ReportSummaryCommand;
import com.optifi.domain.shared.Currency;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ReportSummaryRequestDto(
        Instant from,
        Instant to,
        @NotNull Currency currency
) {
    public ReportSummaryCommand toCommand(Long userId) {
        return new ReportSummaryCommand(userId, from, to, currency);
    }
}

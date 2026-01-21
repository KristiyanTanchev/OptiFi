package com.optifi.domain.reporting.api.request;

import com.optifi.domain.reporting.application.command.ReportSummaryCommand;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ReportSummaryRequestDto(
        Instant from,
        Instant to,
        @NotNull String currency
) {
    public ReportSummaryCommand toCommand(Long userId) {
        return ReportSummaryCommand.from(userId, from, to, currency);
    }
}

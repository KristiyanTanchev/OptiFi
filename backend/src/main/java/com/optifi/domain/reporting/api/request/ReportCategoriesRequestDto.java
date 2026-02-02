package com.optifi.domain.reporting.api.request;

import com.optifi.domain.reporting.application.command.ReportCategoriesCommand;
import com.optifi.domain.shared.TransactionType;

import java.time.Instant;

public record ReportCategoriesRequestDto(
        Instant from,
        Instant to,
        TransactionType type,
        Integer limit
) {
    public ReportCategoriesCommand toCommand(long userId) {
        return new ReportCategoriesCommand(userId, from, to, type, limit);
    }
}

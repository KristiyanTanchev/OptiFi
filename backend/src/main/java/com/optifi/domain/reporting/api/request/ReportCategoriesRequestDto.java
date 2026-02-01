package com.optifi.domain.reporting.api.request;

import com.optifi.domain.reporting.application.command.ReportCategoriesCommand;

import java.time.Instant;

public record ReportCategoriesRequestDto(
        Instant from,
        Instant to,
        String type,
        Integer limit
) {
    public ReportCategoriesCommand toCommand(long userId) {
        return ReportCategoriesCommand.from(userId, from, to, type, limit);
    }
}

package com.optifi.domain.reporting.application.command;

import java.time.Instant;

public record ReportCategoriesCommand(
        Long userId,
        Instant from,
        Instant to,
        String type,
        Integer limit
) {
    public static ReportCategoriesCommand from(long userId, Instant from, Instant to, String type, Integer limit) {
        return new ReportCategoriesCommand(userId, from, to, type, limit);
    }
}

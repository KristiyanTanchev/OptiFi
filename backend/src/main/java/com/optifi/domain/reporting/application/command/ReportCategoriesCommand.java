package com.optifi.domain.reporting.application.command;

import com.optifi.domain.shared.TransactionType;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ReportCategoriesCommand(
        Long userId,
        Instant from,
        Instant to,
        TransactionType type,
        Integer limit
) {
}

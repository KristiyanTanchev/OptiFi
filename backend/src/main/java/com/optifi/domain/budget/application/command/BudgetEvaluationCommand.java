package com.optifi.domain.budget.application.command;

import lombok.Builder;

import java.time.LocalDate;
import java.time.ZoneId;

@Builder
public record BudgetEvaluationCommand(
        Long userId,
        LocalDate from,
        LocalDate to,
        ZoneId zoneId
) {
}

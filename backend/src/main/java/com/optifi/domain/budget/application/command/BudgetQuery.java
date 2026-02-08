package com.optifi.domain.budget.application.command;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record BudgetQuery(
        Long userId,
        LocalDate activeOn,
        LocalDate startDate,
        LocalDate endDate,
        Boolean archived
) {
}

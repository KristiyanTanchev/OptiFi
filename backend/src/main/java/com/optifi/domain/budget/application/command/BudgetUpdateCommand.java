package com.optifi.domain.budget.application.command;

import com.optifi.domain.shared.BudgetPeriod;
import com.optifi.domain.shared.Currency;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record BudgetUpdateCommand(
        Long userId,
        Long budgetId,
        String name,
        BudgetPeriod budgetPeriod,
        BigDecimal amount,
        Currency currency,
        LocalDate startDate,
        LocalDate endDate
) {
}

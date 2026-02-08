package com.optifi.domain.budget.application.command;

import com.optifi.domain.shared.BudgetPeriod;
import com.optifi.domain.shared.Currency;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
public record BudgetCreateCommand(
        Long userId,
        String name,
        BudgetPeriod budgetPeriod,
        BigDecimal amount,
        Currency currency,
        LocalDate startDate,
        LocalDate endDate,
        List<Long> accountIds,
        List<Long> categoryIds
) {
}

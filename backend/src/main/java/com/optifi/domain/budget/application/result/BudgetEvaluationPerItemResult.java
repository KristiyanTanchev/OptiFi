package com.optifi.domain.budget.application.result;

import com.optifi.domain.budget.model.Budget;
import com.optifi.domain.shared.Currency;
import lombok.Builder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Builder
public record BudgetEvaluationPerItemResult(
        Long id,
        String name,
        Currency currency,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal amount,
        BigDecimal spent,
        BigDecimal remaining,
        BigDecimal percentage
) {
    public static BudgetEvaluationPerItemResult fromEntity(Budget budget, BigDecimal spent) {
        BigDecimal amount = budget.getAmount() == null ? BigDecimal.ZERO : budget.getAmount();
        BigDecimal safeSpent = spent == null ? BigDecimal.ZERO : spent;

        BigDecimal remaining = amount.subtract(safeSpent);

        BigDecimal ratio = BigDecimal.ZERO;
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            ratio = safeSpent.divide(amount, 4, RoundingMode.HALF_UP);
        }

        return BudgetEvaluationPerItemResult.builder()
                .id(budget.getId())
                .name(budget.getName())
                .currency(budget.getCurrency())
                .startDate(budget.getStartDate())
                .endDate(budget.getEndDate())
                .amount(amount)
                .spent(safeSpent)
                .remaining(remaining)
                .percentage(ratio)
                .build();
    }
}

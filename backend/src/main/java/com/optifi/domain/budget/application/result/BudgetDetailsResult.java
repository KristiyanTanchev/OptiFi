package com.optifi.domain.budget.application.result;

import com.optifi.domain.budget.model.Budget;
import com.optifi.domain.shared.BudgetPeriod;
import com.optifi.domain.shared.Currency;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Builder
public record BudgetDetailsResult(
        Long id,
        String name,
        BudgetPeriod period,
        BigDecimal amount,
        Currency currency,
        LocalDate startDate,
        LocalDate endDate,
        Instant createdAt,
        Instant updatedAt,
        boolean archived
) {
    public static BudgetDetailsResult fromEntity(Budget budget) {
        return BudgetDetailsResult.builder()
                .id(budget.getId())
                .name(budget.getName())
                .period(budget.getPeriod())
                .amount(budget.getAmount())
                .currency(budget.getCurrency())
                .startDate(budget.getStartDate())
                .endDate(budget.getEndDate())
                .createdAt(budget.getCreatedAt())
                .updatedAt(budget.getUpdatedAt())
                .archived(budget.isArchived())
                .build();
    }
}

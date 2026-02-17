package com.optifi.domain.budget.api.response;

import com.optifi.domain.shared.Currency;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record BudgetEvaluationPerItemResponseDto(
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
}

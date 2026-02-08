package com.optifi.domain.budget.api.response;

import com.optifi.domain.shared.Currency;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BudgetEvaluationPerItemResponseDto(
        Long id,
        String name,
        Currency currency,
        BigDecimal amount,
        BigDecimal spent,
        BigDecimal remaining,
        BigDecimal percentage
) {
}

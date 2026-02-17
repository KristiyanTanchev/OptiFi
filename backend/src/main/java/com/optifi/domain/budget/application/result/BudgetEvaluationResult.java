package com.optifi.domain.budget.application.result;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record BudgetEvaluationResult(
        LocalDate from,
        LocalDate to,
        List<BudgetEvaluationPerItemResult> items
) {
}

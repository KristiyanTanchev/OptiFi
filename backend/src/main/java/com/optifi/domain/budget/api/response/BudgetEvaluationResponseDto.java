package com.optifi.domain.budget.api.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record BudgetEvaluationResponseDto(
        LocalDate from,
        LocalDate to,
        List<BudgetEvaluationPerItemResponseDto> items
) {
}

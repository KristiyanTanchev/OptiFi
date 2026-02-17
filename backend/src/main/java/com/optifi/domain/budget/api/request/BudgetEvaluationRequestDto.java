package com.optifi.domain.budget.api.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BudgetEvaluationRequestDto(
        @NotNull
        LocalDate from,

        @NotNull
        LocalDate to
) {
}

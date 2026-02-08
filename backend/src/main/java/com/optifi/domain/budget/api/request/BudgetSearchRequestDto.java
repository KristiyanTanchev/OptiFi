package com.optifi.domain.budget.api.request;

import java.time.LocalDate;

public record BudgetSearchRequestDto(
        LocalDate activeOn,
        LocalDate startDate,
        LocalDate endDate,
        Boolean archived
) {
}

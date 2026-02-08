package com.optifi.domain.budget.api.request;

import com.optifi.domain.shared.BudgetPeriod;
import com.optifi.domain.shared.Currency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BudgetCreateRequestDto(
        @NotNull(message = "Name is required")
        @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
        String name,

        @NotNull(message = "Budget period is required")
        BudgetPeriod budgetPeriod,

        @NotNull(message = "Amount is required")
        @Min(value = 1, message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "Currency is required")
        Currency currency,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate,

        List<Long> accountIds,

        List<Long> categoryIds
) {
}

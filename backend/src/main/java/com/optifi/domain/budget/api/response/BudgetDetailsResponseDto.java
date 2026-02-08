package com.optifi.domain.budget.api.response;

import com.optifi.domain.shared.BudgetPeriod;
import com.optifi.domain.shared.Currency;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Builder
public record BudgetDetailsResponseDto(
        Long id,
        String name,
        BudgetPeriod period,
        BigDecimal amount,
        Currency currency,
        LocalDate startDate,
        LocalDate endDate,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        boolean archived
) {
}

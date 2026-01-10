package com.optifi.domain.transaction.api.request;

import com.optifi.domain.transaction.application.command.TransactionQuery;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GetUserTransactionsRequestDto(
        @NotNull @Positive Long accountId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal min,
        BigDecimal max,
        String description
) {
    public TransactionQuery toQuery(Long userId) {
        return new TransactionQuery(userId, accountId, startDate, endDate, min, max, description);
    }
}

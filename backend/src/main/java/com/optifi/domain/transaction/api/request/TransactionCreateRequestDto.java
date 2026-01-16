package com.optifi.domain.transaction.api.request;

import com.optifi.domain.transaction.application.command.TransactionCreateCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionCreateRequestDto(
        @NotNull BigDecimal amount,
        @NotNull Instant occurredAt,
        String description,
        @NotNull @Positive Long categoryId
) {
    public TransactionCreateCommand toCreateCommand(Long userId, Long accountId) {
        return new TransactionCreateCommand(userId, accountId, amount, description, occurredAt, categoryId);
    }
}

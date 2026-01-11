package com.optifi.domain.transaction.api.request;

import com.optifi.domain.transaction.application.command.TransactionCreateCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionCreateRequestDto(
        @NotNull @Positive Long accountId,
        @NotNull BigDecimal amount,
        @NotNull @Pattern(regexp = "[0-9]{4}-[0-9]{2}-[0-9]{2}") String occurredAt,
        String description
) {
    public TransactionCreateCommand toCreateCommand(Long userId) {
        return TransactionCreateCommand.from(userId, accountId, amount, description, occurredAt);
    }
}

package com.optifi.domain.transaction.api.request;

import com.optifi.domain.transaction.application.command.TransactionUpdateCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionUpdateRequestDto(
        @NotNull BigDecimal amount,
        @NotNull Instant occurredAt,
        String description,
        @NotNull @Positive Long categoryId
) {
    public TransactionUpdateCommand toUpdateCommand(Long accountId, Long transactionId, Long userId) {
        return new TransactionUpdateCommand(
                userId,
                accountId,
                transactionId,
                amount,
                description,
                occurredAt,
                categoryId
        );
    }
}

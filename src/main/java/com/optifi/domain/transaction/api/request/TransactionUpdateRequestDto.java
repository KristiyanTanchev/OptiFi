package com.optifi.domain.transaction.api.request;

import com.optifi.domain.transaction.application.command.TransactionUpdateCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record TransactionUpdateRequestDto(
        @NotNull BigDecimal amount,
        @NotNull @Pattern(regexp = "[0-9]{4}-[0-9]{2}-[0-9]{2}") String occurredAt,
        String description
) {

    public TransactionUpdateCommand toUpdateCommand(Long transactionId, Long userId) {
        return TransactionUpdateCommand.from(transactionId, userId, amount, description, occurredAt);
    }

}

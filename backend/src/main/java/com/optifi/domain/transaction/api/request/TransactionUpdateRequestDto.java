package com.optifi.domain.transaction.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionUpdateRequestDto(
        @NotNull BigDecimal amount,
        @NotNull OffsetDateTime occurredAt,
        String description,
        @NotNull @Positive Long categoryId
) {
}

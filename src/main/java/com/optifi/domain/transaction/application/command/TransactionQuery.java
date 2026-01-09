package com.optifi.domain.transaction.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionQuery(
        Long userId,
        Long accountId,
        LocalDate from,
        LocalDate to,
        BigDecimal min,
        BigDecimal max,
        String description
) {
}

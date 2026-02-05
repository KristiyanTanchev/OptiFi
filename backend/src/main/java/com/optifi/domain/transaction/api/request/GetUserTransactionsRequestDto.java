package com.optifi.domain.transaction.api.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GetUserTransactionsRequestDto(
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal min,
        BigDecimal max,
        String description
) {
}

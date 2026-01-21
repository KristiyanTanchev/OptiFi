package com.optifi.domain.transaction.application.result;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionGetSummaryResult(
        Long accountId,
        String currency,
        Instant from,
        Instant to,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal net,
        Long count
) {
}

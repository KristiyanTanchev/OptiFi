package com.optifi.domain.transaction.repository;

import java.math.BigDecimal;
import java.time.Instant;

public interface TransactionTimeAndAmount {
    Instant getOccurredAt();

    BigDecimal getAmount();
}

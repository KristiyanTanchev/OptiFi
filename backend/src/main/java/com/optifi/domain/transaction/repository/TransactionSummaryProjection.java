package com.optifi.domain.transaction.repository;

import java.math.BigDecimal;

public interface TransactionSummaryProjection {
    BigDecimal getIncome();

    BigDecimal getExpense();

    Long getCount();
}

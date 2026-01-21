package com.optifi.domain.transaction.repository;


import java.math.BigDecimal;

public interface ReportSummaryProjection {
    BigDecimal getIncome();

    BigDecimal getExpense();

    Long getCount();
}

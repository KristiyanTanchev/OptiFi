package com.optifi.domain.transaction.repository;


import java.math.BigDecimal;

public interface ReportSummaryByAccountProjection {
    Long getAccountId();

    String getAccountName();

    BigDecimal getIncome();

    BigDecimal getExpense();

    Long getCount();
}

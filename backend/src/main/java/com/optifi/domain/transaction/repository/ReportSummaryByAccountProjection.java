package com.optifi.domain.transaction.repository;


import java.math.BigDecimal;

public interface ReportSummaryByAccountProjection {
    Long accountId();

    String accountName();

    BigDecimal income();

    BigDecimal expense();

    Long count();
}

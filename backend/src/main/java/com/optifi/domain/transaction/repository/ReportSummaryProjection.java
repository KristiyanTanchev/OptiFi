package com.optifi.domain.transaction.repository;


import com.optifi.domain.shared.model.Currency;

import java.math.BigDecimal;

public interface ReportSummaryProjection {
    Currency currency();

    BigDecimal income();

    BigDecimal expense();

    Long count();
}

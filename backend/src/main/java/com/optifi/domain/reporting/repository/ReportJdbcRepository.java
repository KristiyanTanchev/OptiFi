package com.optifi.domain.reporting.repository;

import com.optifi.domain.shared.model.Currency;

import java.time.Instant;
import java.util.List;

public interface ReportJdbcRepository {
    ReportSummaryAgg getReportSummary(
            Long userId,
            Currency currency,
            Instant from,
            Instant to);

    List<ReportSummaryByAccountAgg> getReportSummaryByAccount(
            Long userId,
            Currency currency,
            Instant from,
            Instant to);
}

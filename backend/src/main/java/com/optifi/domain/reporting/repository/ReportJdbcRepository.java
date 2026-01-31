package com.optifi.domain.reporting.repository;

import com.optifi.domain.reporting.repository.aggregations.ReportCategoriesAgg;
import com.optifi.domain.reporting.repository.aggregations.ReportCategoriesByCatAgg;
import com.optifi.domain.reporting.repository.aggregations.ReportSummaryAgg;
import com.optifi.domain.reporting.repository.aggregations.ReportSummaryByAccountAgg;
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

    ReportCategoriesAgg getReportCategories(
            Long userid,
            Instant from,
            Instant to
    );

    List<ReportCategoriesByCatAgg> getReportCategoriesByCat(
            Long userId,
            Instant from,
            Instant to
    );
}

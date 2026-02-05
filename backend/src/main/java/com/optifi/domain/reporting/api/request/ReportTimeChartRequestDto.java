package com.optifi.domain.reporting.api.request;

import com.optifi.domain.shared.TimeBucket;
import com.optifi.domain.shared.TransactionType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReportTimeChartRequestDto(
        @NotNull TimeBucket bucket,
        @NotNull TransactionType type,
        @NotNull LocalDate from,
        @NotNull LocalDate to
) {
}

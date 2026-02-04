package com.optifi.domain.reporting.api.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record ReportTimeChartByPeriodResponseDto(
        LocalDate date,
        BigDecimal amount
) {
}

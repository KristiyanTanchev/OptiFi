package com.optifi.domain.reporting.api.response;

import com.optifi.domain.reporting.application.result.ReportTimeChartByPeriodResult;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReportTimeChartByPeriodResponseDto(
        LocalDate date,
        BigDecimal amount
) {
    public static ReportTimeChartByPeriodResponseDto from(ReportTimeChartByPeriodResult result) {
        return new ReportTimeChartByPeriodResponseDto(
                result.getDate(),
                result.getAmount()
        );
    }
}

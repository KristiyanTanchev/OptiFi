package com.optifi.domain.reporting.api.response;

import com.optifi.domain.reporting.application.result.ReportCategoriesResult;

import java.math.BigDecimal;
import java.util.List;

public record ReportCategoriesResponseDto(
        String currency,
        String type,
        BigDecimal total,
        List<ReportCategoriesByCatResponseDto> items
) {
    public static ReportCategoriesResponseDto from(ReportCategoriesResult result) {
        return new ReportCategoriesResponseDto(
                result.currency(),
                result.type(),
                result.total(),
                result.items().stream().map(ReportCategoriesByCatResponseDto::from).toList()
        );
    }
}

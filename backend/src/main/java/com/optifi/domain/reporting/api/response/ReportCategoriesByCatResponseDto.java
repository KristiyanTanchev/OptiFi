package com.optifi.domain.reporting.api.response;

import com.optifi.domain.reporting.application.result.ReportCategoriesByCatResult;

import java.math.BigDecimal;

public record ReportCategoriesByCatResponseDto(
        Long categoryId,
        String categoryName,
        String icon,
        BigDecimal amount,
        BigDecimal percent
) {
    public static ReportCategoriesByCatResponseDto from(ReportCategoriesByCatResult result) {
        return new ReportCategoriesByCatResponseDto(
                result.categoryId(),
                result.categoryName(),
                result.icon(),
                result.amount(),
                result.percent()
        );
    }
}

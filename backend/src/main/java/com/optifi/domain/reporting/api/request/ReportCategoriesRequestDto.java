package com.optifi.domain.reporting.api.request;

import com.optifi.domain.shared.TransactionType;

import java.time.LocalDate;

public record ReportCategoriesRequestDto(
        LocalDate from,
        LocalDate to,
        TransactionType type,
        Integer limit
) {
}

package com.optifi.domain.reporting.api.request;

import com.optifi.domain.shared.Currency;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReportSummaryRequestDto(
        LocalDate from,
        LocalDate to,
        @NotNull Currency currency
) {
}

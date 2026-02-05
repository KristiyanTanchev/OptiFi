package com.optifi.domain.reporting.api.response;

import com.optifi.domain.shared.Currency;
import com.optifi.domain.shared.TransactionType;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ReportCategoriesResponseDto(
        Currency currency,
        TransactionType type,
        BigDecimal total,
        List<ReportCategoriesByCatResponseDto> items
) {
}

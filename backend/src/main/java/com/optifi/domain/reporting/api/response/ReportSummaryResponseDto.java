package com.optifi.domain.reporting.api.response;

import com.optifi.domain.shared.Currency;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ReportSummaryResponseDto(
        Currency currency,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal net,
        Long count,
        List<ReportSummaryByAccountResponseDto> byAccount
) {
}

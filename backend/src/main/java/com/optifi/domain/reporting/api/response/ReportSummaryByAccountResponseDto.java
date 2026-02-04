package com.optifi.domain.reporting.api.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ReportSummaryByAccountResponseDto(
        Long accountId,
        String accountName,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal net,
        Long count
) {
}

package com.optifi.domain.transaction.api.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record TransactionGetSummaryResponseDto(
        Long accountId,
        String currency,
        LocalDate from,
        LocalDate to,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal net,
        Long count
) {
}

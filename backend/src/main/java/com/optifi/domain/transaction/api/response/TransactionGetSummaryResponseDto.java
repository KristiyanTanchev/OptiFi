package com.optifi.domain.transaction.api.response;

import com.optifi.domain.transaction.application.result.TransactionGetSummaryResult;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionGetSummaryResponseDto(
        Long accountId,
        String currency,
        Instant from,
        Instant to,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal net,
        Long count
) {
    public static TransactionGetSummaryResponseDto from(TransactionGetSummaryResult result) {
        return new TransactionGetSummaryResponseDto(
                result.accountId(),
                result.currency(),
                result.from(),
                result.to(),
                result.income(),
                result.expense(),
                result.net(),
                result.count()
        );
    }
}

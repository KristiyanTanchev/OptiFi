package com.optifi.domain.transaction.application.result;

import com.optifi.domain.shared.model.Currency;
import com.optifi.domain.transaction.application.command.TransactionGetSummaryCommand;
import com.optifi.domain.transaction.repository.TransactionSummaryProjection;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionGetSummaryResult(
        Long accountId,
        String currency,
        Instant from,
        Instant to,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal net,
        Long count
) {
    public static TransactionGetSummaryResult from(
            Currency currency,
            TransactionGetSummaryCommand cmd,
            TransactionSummaryProjection projection) {
        return new TransactionGetSummaryResult(
                cmd.accountId(),
                currency.name(),
                cmd.from(),
                cmd.to(),
                projection.getIncome(),
                projection.getExpense(),
                projection.getIncome().subtract(projection.getExpense()),
                projection.getCount()
        );
    }
}

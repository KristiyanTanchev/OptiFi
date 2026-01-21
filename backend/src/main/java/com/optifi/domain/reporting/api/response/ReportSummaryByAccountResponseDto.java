package com.optifi.domain.reporting.api.response;

import com.optifi.domain.reporting.application.result.ReportSummaryByAccountResult;

import java.math.BigDecimal;

public record ReportSummaryByAccountResponseDto(
        Long accountId,
        String accountName,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal net,
        Long count
) {
    public static ReportSummaryByAccountResponseDto from(ReportSummaryByAccountResult result) {
        return new ReportSummaryByAccountResponseDto(
                result.accountId(),
                result.accountName(),
                result.income(),
                result.expense(),
                result.net(),
                result.count()
        );
    }
}

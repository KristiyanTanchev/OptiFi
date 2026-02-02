package com.optifi.domain.reporting.api.response;

import com.optifi.domain.reporting.application.result.ReportSummaryResult;
import com.optifi.domain.shared.Currency;

import java.math.BigDecimal;
import java.util.List;

public record ReportSummaryResponseDto(
        Currency currency,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal net,
        Long count,
        List<ReportSummaryByAccountResponseDto> byAccount
) {

    public static ReportSummaryResponseDto from(ReportSummaryResult result) {
        return new ReportSummaryResponseDto(
                result.currency(),
                result.income(),
                result.expense(),
                result.net(),
                result.count(),
                result.byAccount().stream().map(ReportSummaryByAccountResponseDto::from).toList()
        );
    }
}

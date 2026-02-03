package com.optifi.domain.reporting.application.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

//Using class instead of record because initializing and then setting amount in service
@Getter
@Setter
@AllArgsConstructor
public class ReportTimeChartByPeriodResult {
    private LocalDate date;
    private BigDecimal amount;
}

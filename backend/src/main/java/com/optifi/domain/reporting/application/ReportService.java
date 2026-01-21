package com.optifi.domain.reporting.application;

import com.optifi.domain.reporting.application.command.ReportSummaryCommand;
import com.optifi.domain.reporting.application.result.ReportSummaryResult;

public interface ReportService {
    ReportSummaryResult getReportSummary(ReportSummaryCommand cmd);
}

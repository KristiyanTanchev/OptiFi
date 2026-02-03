package com.optifi.domain.reporting.application;

import com.optifi.domain.reporting.application.command.ReportCategoriesCommand;
import com.optifi.domain.reporting.application.command.ReportSummaryCommand;
import com.optifi.domain.reporting.application.command.ReportTimeChartCommand;
import com.optifi.domain.reporting.application.result.ReportCategoriesResult;
import com.optifi.domain.reporting.application.result.ReportSummaryResult;
import com.optifi.domain.reporting.application.result.ReportTimeChartResult;

public interface ReportService {
    ReportSummaryResult getReportSummary(ReportSummaryCommand cmd);

    ReportCategoriesResult getReportCategories(ReportCategoriesCommand cmd);

    ReportTimeChartResult getReportTimeChart(ReportTimeChartCommand cmd);
}

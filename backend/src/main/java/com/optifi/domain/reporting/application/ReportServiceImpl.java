package com.optifi.domain.reporting.application;

import com.optifi.domain.reporting.application.command.ReportCategoriesCommand;
import com.optifi.domain.reporting.application.command.ReportSummaryCommand;
import com.optifi.domain.reporting.application.result.ReportCategoriesResult;
import com.optifi.domain.reporting.application.result.ReportSummaryResult;
import com.optifi.domain.reporting.repository.ReportJdbcRepository;
import com.optifi.domain.reporting.repository.aggregations.ReportSummaryAgg;
import com.optifi.domain.reporting.repository.aggregations.ReportSummaryByAccountAgg;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportJdbcRepository reportJdbcRepository;

    @Override
    @Transactional(readOnly = true)
    public ReportSummaryResult getReportSummary(ReportSummaryCommand cmd) {
        ReportSummaryAgg reportSummary = reportJdbcRepository.getReportSummary(
                cmd.userId(),
                cmd.currency(),
                cmd.from(),
                cmd.to()
        );
        List<ReportSummaryByAccountAgg> byAccount = reportJdbcRepository.getReportSummaryByAccount(
                cmd.userId(),
                cmd.currency(),
                cmd.from(),
                cmd.to()
        );
        return ReportSummaryResult.from(cmd.currency(), reportSummary, byAccount);
    }

    @Override
    public ReportCategoriesResult getReportCategories(ReportCategoriesCommand cmd) {
        //TODO: implement
        return null;
    }
}

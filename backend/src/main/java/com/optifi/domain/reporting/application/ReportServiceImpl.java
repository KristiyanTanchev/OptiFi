package com.optifi.domain.reporting.application;

import com.optifi.domain.reporting.application.command.ReportSummaryCommand;
import com.optifi.domain.reporting.application.result.ReportSummaryResult;
import com.optifi.domain.reporting.repository.ReportJdbcRepository;
import com.optifi.domain.reporting.repository.ReportSummaryAgg;
import com.optifi.domain.reporting.repository.ReportSummaryByAccountAgg;
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
}

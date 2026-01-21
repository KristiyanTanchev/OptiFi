package com.optifi.domain.reporting.application;

import com.optifi.domain.reporting.application.command.ReportSummaryCommand;
import com.optifi.domain.reporting.application.result.ReportSummaryResult;
import com.optifi.domain.transaction.repository.ReportSummaryByAccountProjection;
import com.optifi.domain.transaction.repository.ReportSummaryProjection;
import com.optifi.domain.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
    public ReportSummaryResult getReportSummary(ReportSummaryCommand cmd) {
        ReportSummaryProjection summaryProjection = transactionRepository.getReportSummary(
                cmd.userId(),
                cmd.from(),
                cmd.to(),
                cmd.currency()
        );
        List<ReportSummaryByAccountProjection> byAccount = transactionRepository.getReportSummaryByAccount(
                cmd.userId(),
                cmd.from(),
                cmd.to(),
                cmd.currency()
        );
        return ReportSummaryResult.from(summaryProjection, byAccount);
    }
}

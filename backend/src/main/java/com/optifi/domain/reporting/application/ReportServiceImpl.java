package com.optifi.domain.reporting.application;

import com.optifi.domain.reporting.application.command.ReportCategoriesCommand;
import com.optifi.domain.reporting.application.command.ReportSummaryCommand;
import com.optifi.domain.reporting.application.result.ReportCategoriesResult;
import com.optifi.domain.reporting.application.result.ReportSummaryResult;
import com.optifi.domain.reporting.repository.ReportJdbcRepository;
import com.optifi.domain.reporting.repository.aggregations.ReportCategoriesAgg;
import com.optifi.domain.reporting.repository.aggregations.ReportCategoriesByCatAgg;
import com.optifi.domain.reporting.repository.aggregations.ReportSummaryAgg;
import com.optifi.domain.reporting.repository.aggregations.ReportSummaryByAccountAgg;
import com.optifi.domain.shared.model.TransactionType;
import com.optifi.domain.user.model.User;
import com.optifi.domain.user.repository.UserRepository;
import com.optifi.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportJdbcRepository reportJdbcRepository;
    private final UserRepository userRepository;

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
        Integer sign = null;
        if (cmd.type() == TransactionType.EXPENSE){
            sign = -1;
        }else if (cmd.type() == TransactionType.INCOME){
            sign = 1;
        }
        User user = userRepository.findById(cmd.userId()).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        ReportCategoriesAgg reportCategoriesAgg = reportJdbcRepository.getReportCategories(
                cmd.userId(),
                sign,
                cmd.from(),
                cmd.to()
        );
        List<ReportCategoriesByCatAgg> byCat = reportJdbcRepository.getReportCategoriesByCat(
                cmd.userId(),
                sign,
                cmd.from(),
                cmd.to()
        );

        return ReportCategoriesResult.from(
                user.getBaseCurrency(),
                cmd.type().name().toLowerCase(),
                reportCategoriesAgg,
                byCat
        );
    }
}

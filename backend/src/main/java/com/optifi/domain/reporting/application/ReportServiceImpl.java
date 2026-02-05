package com.optifi.domain.reporting.application;

import com.optifi.domain.reporting.application.command.ReportCategoriesCommand;
import com.optifi.domain.reporting.application.command.ReportSummaryCommand;
import com.optifi.domain.reporting.application.command.ReportTimeChartCommand;
import com.optifi.domain.reporting.application.result.ReportCategoriesResult;
import com.optifi.domain.reporting.application.result.ReportSummaryResult;
import com.optifi.domain.reporting.application.result.ReportTimeChartByPeriodResult;
import com.optifi.domain.reporting.application.result.ReportTimeChartResult;
import com.optifi.domain.reporting.repository.ReportJdbcRepository;
import com.optifi.domain.reporting.repository.aggregations.*;
import com.optifi.domain.shared.TimeBucket;
import com.optifi.domain.shared.TimeHelper;
import com.optifi.domain.shared.TransactionType;
import com.optifi.domain.transaction.repository.TransactionTimeAndAmount;
import com.optifi.domain.transaction.repository.TransactionRepository;
import com.optifi.domain.user.model.User;
import com.optifi.domain.user.repository.UserRepository;
import com.optifi.exceptions.EntityNotFoundException;
import com.optifi.exceptions.InvalidDateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportJdbcRepository reportJdbcRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TimeHelper timeHelper;

    private static final Integer MIN_INTERVAL = 2;
    private static final Integer MAX_INTERVAL = 50;

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
        if (cmd.type() == TransactionType.EXPENSE) {
            sign = -1;
        } else if (cmd.type() == TransactionType.INCOME) {
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
                cmd.type(),
                reportCategoriesAgg,
                byCat
        );
    }

    @Override
    public ReportTimeChartResult getReportTimeChart(ReportTimeChartCommand cmd) {

        validateTimeInterval(cmd.from(), cmd.to());

        LocalDate fromKey = normalizeToBucketStart(cmd.from(), cmd.bucket());
        LocalDate toKey = normalizeToBucketStart(cmd.to(), cmd.bucket());

        List<ReportTimeChartByPeriodResult> buckets = initializeInterval(
                fromKey,
                toKey,
                cmd.bucket()
        );

        Map<LocalDate, ReportTimeChartByPeriodResult> bucketByDate = new HashMap<>();
        for (ReportTimeChartByPeriodResult b : buckets) {
            b.setAmount(BigDecimal.ZERO);
            bucketByDate.put(b.getDate(), b);
        }

        Instant start = timeHelper.startOfDay(cmd.from(), cmd.zoneId());
        Instant endExclusive = timeHelper.startOfNextDay(cmd.to(), cmd.zoneId());

        List<TransactionTimeAndAmount> transactions = switch (cmd.type()) {
            case INCOME -> transactionRepository
                    .findByAccount_User_IdAndAmountGreaterThanAndOccurredAtGreaterThanEqualAndOccurredAtLessThanOrderByOccurredAt(
                            cmd.userId(), BigDecimal.ZERO, start, endExclusive);
            case EXPENSE -> transactionRepository
                    .findByAccount_User_IdAndAmountLessThanAndOccurredAtGreaterThanEqualAndOccurredAtLessThanOrderByOccurredAt(
                            cmd.userId(), BigDecimal.ZERO, start, endExclusive);
            case ANY -> transactionRepository
                    .findByAccount_User_IdAndOccurredAtGreaterThanEqualAndOccurredAtLessThanOrderByOccurredAt(
                            cmd.userId(), start, endExclusive);
        };

        for (TransactionTimeAndAmount tx : transactions) {
            LocalDate txDate = timeHelper.toLocalDate(tx.getOccurredAt(), cmd.zoneId());
            LocalDate key = normalizeToBucketStart(txDate, cmd.bucket());

            ReportTimeChartByPeriodResult bucket = bucketByDate.get(key);

            BigDecimal value = tx.getAmount();
            if (cmd.type() == TransactionType.EXPENSE) {
                value = value.abs(); // make expenses positive for charts
            }

            bucket.setAmount(bucket.getAmount().add(value));
        }

        return new ReportTimeChartResult(cmd.bucket(), cmd.type(), cmd.baseCurrency(), buckets);
    }

    private LocalDate normalizeToBucketStart(LocalDate date, TimeBucket bucket) {
        return switch (bucket) {
            case DAY -> date;
            case WEEK -> date.with(DayOfWeek.MONDAY);
            case MONTH -> date.withDayOfMonth(1);
            case YEAR -> date.withDayOfYear(1);
        };
    }

    private void validateTimeInterval(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new InvalidDateException("From and to dates are required");
        }
        if (start.isAfter(end)) {
            throw new InvalidDateException("From date must be before to date");
        }
    }

    private List<ReportTimeChartByPeriodResult> initializeInterval(
            LocalDate start,
            LocalDate end,
            TimeBucket bucket
    ) {
        long steps = switch (bucket) {
            case DAY -> ChronoUnit.DAYS.between(start, end) + 1;
            case WEEK -> ChronoUnit.WEEKS.between(start, end) + 1;
            case MONTH -> ChronoUnit.MONTHS.between(start, end) + 1;
            case YEAR -> ChronoUnit.YEARS.between(start, end) + 1;
        };

        if (steps < ReportServiceImpl.MIN_INTERVAL || steps > ReportServiceImpl.MAX_INTERVAL) {
            throw new InvalidDateException(String.format(
                    "Interval must be between %d and %d %s.",
                    ReportServiceImpl.MIN_INTERVAL, ReportServiceImpl.MAX_INTERVAL, bucket.name().toLowerCase()
            ));
        }

        List<ReportTimeChartByPeriodResult> result = new ArrayList<>((int) steps);
        LocalDate cursor = start;

        for (int i = 0; i < steps; i++) {
            result.add(new ReportTimeChartByPeriodResult(cursor, BigDecimal.ZERO));
            cursor = switch (bucket) {
                case DAY -> cursor.plusDays(1);
                case WEEK -> cursor.plusWeeks(1);
                case MONTH -> cursor.plusMonths(1);
                case YEAR -> cursor.plusYears(1);
            };
        }

        return result;
    }
}

package com.optifi.domain.reporting;

import com.optifi.domain.reporting.application.ReportServiceImpl;
import com.optifi.domain.reporting.application.command.ReportCategoriesCommand;
import com.optifi.domain.reporting.application.command.ReportSummaryCommand;
import com.optifi.domain.reporting.application.command.ReportTimeChartCommand;
import com.optifi.domain.reporting.application.result.ReportSummaryResult;
import com.optifi.domain.reporting.repository.ReportJdbcRepository;
import com.optifi.domain.shared.*;
import com.optifi.domain.transaction.repository.TransactionRepository;
import com.optifi.domain.transaction.repository.TransactionTimeAndAmount;
import com.optifi.domain.user.model.User;
import com.optifi.domain.user.repository.UserRepository;
import com.optifi.exceptions.EntityNotFoundException;
import com.optifi.exceptions.InvalidDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTests {

    @Mock
    private ReportJdbcRepository reportJdbcRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TimeHelper timeHelper;

    @InjectMocks
    private ReportServiceImpl reportService;

    private User user1;
    private final ZoneId zoneId = ZoneId.of("UTC");

    @BeforeEach
    void setUp() {
        user1 = User.builder().id(1L).build();
    }

    @Test
    void getReportSummary_Should_returnEmptyResult_When_empty() {
        ReportSummaryCommand cmd = ReportSummaryCommand.builder().build();
        when(reportJdbcRepository.getReportSummary(cmd.userId(), cmd.currency(), cmd.from(), cmd.to()))
                .thenReturn(null);
        when(reportJdbcRepository.getReportSummaryByAccount(cmd.userId(), cmd.currency(), cmd.from(), cmd.to()))
                .thenReturn(null);
        ReportSummaryResult result = reportService.getReportSummary(cmd);
        verify(reportJdbcRepository).getReportSummary(cmd.userId(), cmd.currency(), cmd.from(), cmd.to());
        verify(reportJdbcRepository).getReportSummaryByAccount(cmd.userId(), cmd.currency(), cmd.from(), cmd.to());
        assertEquals(0, result.count());
        assertEquals(0, result.byAccount().size());
    }

    @Test
    void getReportCategories_Should_throw_When_userNotFound() {
        ReportCategoriesCommand cmd = ReportCategoriesCommand.builder()
                .userId(99L)
                .build();
        when(userRepository.findById(cmd.userId())).thenReturn(java.util.Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> reportService.getReportCategories(cmd));
    }

    @Test
    void getReportCategories_Should_countExpenses_When_typeExpense() {
        ReportCategoriesCommand cmd = ReportCategoriesCommand.builder()
                .userId(1L)
                .type(TransactionType.EXPENSE)
                .build();
        when(userRepository.findById(cmd.userId())).thenReturn(Optional.of(user1));
        when(reportJdbcRepository.getReportCategories(cmd.userId(), -1, cmd.from(), cmd.to())).thenReturn(null);
        var result = reportService.getReportCategories(cmd);
        verify(reportJdbcRepository).getReportCategories(cmd.userId(), -1, cmd.from(), cmd.to());
        assertEquals(cmd.type(), result.type());
    }

    @Test
    void getReportCategories_Should_countIncome_When_typeIncome() {
        ReportCategoriesCommand cmd = ReportCategoriesCommand.builder()
                .userId(1L)
                .type(TransactionType.INCOME)
                .build();
        when(userRepository.findById(cmd.userId())).thenReturn(Optional.of(user1));
        when(reportJdbcRepository.getReportCategories(cmd.userId(), 1, cmd.from(), cmd.to())).thenReturn(null);
        var result = reportService.getReportCategories(cmd);
        verify(reportJdbcRepository).getReportCategories(cmd.userId(), 1, cmd.from(), cmd.to());
        assertEquals(cmd.type(), result.type());
    }

    @Test
    void getReportCategories_Should_countAll_When_typeUnspecified() {
        ReportCategoriesCommand cmd = ReportCategoriesCommand.builder()
                .userId(1L)
                .type(null)
                .build();
        when(userRepository.findById(cmd.userId())).thenReturn(Optional.of(user1));
        when(reportJdbcRepository.getReportCategories(cmd.userId(), null, cmd.from(), cmd.to())).thenReturn(null);
        var result = reportService.getReportCategories(cmd);
        verify(reportJdbcRepository).getReportCategories(cmd.userId(), null, cmd.from(), cmd.to());
        assertEquals(cmd.type(), result.type());
    }

    static Stream<Arguments> invalidDateCases() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, "2022-01-01"),
                Arguments.of("2022-01-01", null),
                Arguments.of("2022-01-02", "2022-01-01")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidDateCases")
    void getReportTimeChart_Should_throw_When_invalidStartOrEnd(LocalDate from, LocalDate to) {
        ReportTimeChartCommand cmd = ReportTimeChartCommand.builder()
                .from(from)
                .to(to)
                .build();
        assertThrows(InvalidDateException.class, () -> reportService.getReportTimeChart(cmd));
    }

    static Stream<Arguments> invalidStepsCases() {
        return Stream.of(
                Arguments.of("2018-01-01", "2018-01-07", TimeBucket.WEEK),
                Arguments.of("2018-01-01", "2018-06-30", TimeBucket.DAY)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidStepsCases")
    void getReportTimeChart_Should_throw_When_stepsAreBelowOrOverLimit(String from, String to, TimeBucket bucket) {
        ReportTimeChartCommand cmd = ReportTimeChartCommand.builder()
                .userId(user1.getId())
                .zoneId(zoneId)
                .baseCurrency(Currency.EUR)
                .type(TransactionType.ANY)
                .from(LocalDate.parse(from))
                .to(LocalDate.parse(to))
                .bucket(bucket)
                .build();
        assertThrows(InvalidDateException.class, () -> reportService.getReportTimeChart(cmd));
    }

    static Stream<Arguments> validDateCases() {
        return Stream.of(
                Arguments.of("2018-01-01", "2018-01-07", TimeBucket.DAY),
                Arguments.of("2018-01-01", "2018-01-14", TimeBucket.WEEK),
                Arguments.of("2018-01-01", "2018-03-31", TimeBucket.MONTH),
                Arguments.of("2018-01-01", "2019-12-31", TimeBucket.YEAR)
        );
    }

    @ParameterizedTest
    @MethodSource("validDateCases")
    void getReportTimeChart_Should_notThrow_When_validStartOrEnd(
            String from, String to, TimeBucket bucket
    ) {
        ReportTimeChartCommand cmd = ReportTimeChartCommand.builder()
                .userId(user1.getId())
                .zoneId(zoneId)
                .baseCurrency(Currency.EUR)
                .type(TransactionType.ANY)
                .from(LocalDate.parse(from))
                .to(LocalDate.parse(to))
                .bucket(bucket)
                .build();
        reportService.getReportTimeChart(cmd);
    }

    @Test
    void getReportTimeChart_Should_sumTransactions_When_validIncome() {
        ReportTimeChartCommand cmd = ReportTimeChartCommand.builder()
                .userId(user1.getId())
                .zoneId(zoneId)
                .baseCurrency(Currency.EUR)
                .type(TransactionType.INCOME)
                .from(LocalDate.parse("2018-01-01"))
                .to(LocalDate.parse("2018-01-07"))
                .bucket(TimeBucket.DAY)
                .build();
        TransactionTimeAndAmount transaction1 = mock(TransactionTimeAndAmount.class);
        TransactionTimeAndAmount transaction2 = mock(TransactionTimeAndAmount.class);
        TransactionTimeAndAmount transaction3 = mock(TransactionTimeAndAmount.class);
        when(transaction1.getAmount()).thenReturn(BigDecimal.valueOf(100));
        when(transaction2.getAmount()).thenReturn(BigDecimal.valueOf(200));
        when(transaction3.getAmount()).thenReturn(BigDecimal.valueOf(300));
        when(transaction1.getOccurredAt()).thenReturn(Instant.parse("2018-01-02T00:00:00Z"));
        when(transaction2.getOccurredAt()).thenReturn(Instant.parse("2018-01-03T00:00:00Z"));
        when(transaction3.getOccurredAt()).thenReturn(Instant.parse("2018-01-02T00:00:00Z"));
        List<TransactionTimeAndAmount> mockedTransactions = List.of(transaction1, transaction2, transaction3);

        when(timeHelper.toLocalDate(transaction1.getOccurredAt(), cmd.zoneId()))
                .thenReturn(LocalDate.parse("2018-01-02"));
        when(timeHelper.toLocalDate(transaction2.getOccurredAt(), cmd.zoneId()))
                .thenReturn(LocalDate.parse("2018-01-03"));
        when(timeHelper.toLocalDate(transaction3.getOccurredAt(), cmd.zoneId()))
                .thenReturn(LocalDate.parse("2018-01-02"));

        when(transactionRepository.findByAccount_User_IdAndAmountGreaterThanAndOccurredAtGreaterThanEqualAndOccurredAtLessThanOrderByOccurredAt(
                cmd.userId(), BigDecimal.ZERO, null, null
        )).thenReturn(mockedTransactions);


        var result = reportService.getReportTimeChart(cmd);

        assertEquals(7, result.points().size());
        assertEquals(transaction1.getAmount().add(transaction3.getAmount()), result.points().get(1).getAmount());
        assertEquals(transaction2.getAmount(), result.points().get(2).getAmount());
    }

    @Test
    void getReportTimeChart_Should_sumTransactionsAndInvert_When_validExpense() {
        ReportTimeChartCommand cmd = ReportTimeChartCommand.builder()
                .userId(user1.getId())
                .zoneId(zoneId)
                .baseCurrency(Currency.EUR)
                .type(TransactionType.EXPENSE)
                .from(LocalDate.parse("2018-01-01"))
                .to(LocalDate.parse("2018-01-07"))
                .bucket(TimeBucket.DAY)
                .build();
        TransactionTimeAndAmount transaction1 = mock(TransactionTimeAndAmount.class);
        TransactionTimeAndAmount transaction2 = mock(TransactionTimeAndAmount.class);
        TransactionTimeAndAmount transaction3 = mock(TransactionTimeAndAmount.class);
        when(transaction1.getAmount()).thenReturn(BigDecimal.valueOf(-100));
        when(transaction2.getAmount()).thenReturn(BigDecimal.valueOf(-200));
        when(transaction3.getAmount()).thenReturn(BigDecimal.valueOf(-300));
        when(transaction1.getOccurredAt()).thenReturn(Instant.parse("2018-01-02T00:00:00Z"));
        when(transaction2.getOccurredAt()).thenReturn(Instant.parse("2018-01-03T00:00:00Z"));
        when(transaction3.getOccurredAt()).thenReturn(Instant.parse("2018-01-02T00:00:00Z"));
        List<TransactionTimeAndAmount> mockedTransactions = List.of(transaction1, transaction2, transaction3);

        when(timeHelper.toLocalDate(transaction1.getOccurredAt(), cmd.zoneId()))
                .thenReturn(LocalDate.parse("2018-01-02"));
        when(timeHelper.toLocalDate(transaction2.getOccurredAt(), cmd.zoneId()))
                .thenReturn(LocalDate.parse("2018-01-03"));
        when(timeHelper.toLocalDate(transaction3.getOccurredAt(), cmd.zoneId()))
                .thenReturn(LocalDate.parse("2018-01-02"));

        when(transactionRepository.findByAccount_User_IdAndAmountLessThanAndOccurredAtGreaterThanEqualAndOccurredAtLessThanOrderByOccurredAt(
                cmd.userId(), BigDecimal.ZERO, null, null
        )).thenReturn(mockedTransactions);


        var result = reportService.getReportTimeChart(cmd);

        assertEquals(7, result.points().size());
        assertEquals(
                transaction1.getAmount().add(transaction3.getAmount()).multiply(BigDecimal.valueOf(-1)),
                result.points().get(1).getAmount());

        assertEquals(
                transaction2.getAmount().multiply(BigDecimal.valueOf(-1)),
                result.points().get(2).getAmount());
    }


}

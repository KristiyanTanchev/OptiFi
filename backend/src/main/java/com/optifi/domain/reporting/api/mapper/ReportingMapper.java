package com.optifi.domain.reporting.api.mapper;

import com.optifi.domain.reporting.api.request.ReportCategoriesRequestDto;
import com.optifi.domain.reporting.api.request.ReportSummaryRequestDto;
import com.optifi.domain.reporting.api.request.ReportTimeChartRequestDto;
import com.optifi.domain.reporting.api.response.*;
import com.optifi.domain.reporting.application.command.ReportCategoriesCommand;
import com.optifi.domain.reporting.application.command.ReportSummaryCommand;
import com.optifi.domain.reporting.application.command.ReportTimeChartCommand;
import com.optifi.domain.reporting.application.result.*;
import com.optifi.domain.shared.TimeHelper;
import com.optifi.domain.shared.TransactionType;
import com.optifi.domain.shared.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportingMapper {
    private final TimeHelper timeHelper;

    public ReportCategoriesCommand toCategoriesCommand(ReportCategoriesRequestDto dto, UserContext ctx) {
        return ReportCategoriesCommand.builder()
                .userId(ctx.userId())
                .from(timeHelper.startOfDay(dto.from(), ctx.zoneId()))
                .to(timeHelper.startOfNextDay(dto.to(), ctx.zoneId()))
                .type(dto.type() != null ? dto.type() : TransactionType.ANY)
                .limit(dto.limit())
                .build();
    }

    public ReportSummaryCommand toSummaryCommand(ReportSummaryRequestDto dto, UserContext ctx) {
        return ReportSummaryCommand.builder()
                .userId(ctx.userId())
                .currency(dto.currency())
                .from(timeHelper.startOfDay(dto.from(), ctx.zoneId()))
                .to(timeHelper.startOfNextDay(dto.to(), ctx.zoneId()))
                .build();
    }

    public ReportTimeChartCommand toTimeChartCommand(ReportTimeChartRequestDto dto, UserContext ctx) {
        return ReportTimeChartCommand.builder()
                .userId(ctx.userId())
                .zoneId(ctx.zoneId())
                .baseCurrency(ctx.currency())
                .bucket(dto.bucket())
                .from(dto.from())
                .to(dto.to())
                .type(dto.type())
                .build();
    }

    public ReportCategoriesResponseDto toCategoriesResponseDto(ReportCategoriesResult result) {
        return ReportCategoriesResponseDto.builder()
                .currency(result.currency())
                .items(result.items().stream().map(this::toCategoriesByCatResponseDto).toList())
                .total(result.total())
                .type(result.type())
                .build();
    }

    public ReportSummaryResponseDto toSummaryResponseDto(ReportSummaryResult result) {
        return ReportSummaryResponseDto.builder()
                .currency(result.currency())
                .income(result.income())
                .expense(result.expense())
                .net(result.net())
                .count(result.count())
                .byAccount(result.byAccount().stream().map(this::toSummaryByAccountResponseDto).toList())
                .build();
    }

    public ReportTimeChartResponseDto toTimeChartResponseDto(ReportTimeChartResult result) {
        return ReportTimeChartResponseDto.builder()
                .bucket(result.bucket())
                .type(result.type())
                .currency(result.currency())
                .points(result.points().stream().map(this::toTimeChartByPeriodResponseDto).toList())
                .build();
    }

    private ReportCategoriesByCatResponseDto toCategoriesByCatResponseDto(ReportCategoriesByCatResult result) {
        return ReportCategoriesByCatResponseDto.builder()
                .categoryId(result.categoryId())
                .categoryName(result.categoryName())
                .icon(result.icon())
                .amount(result.amount())
                .percent(result.percent())
                .build();
    }

    private ReportSummaryByAccountResponseDto toSummaryByAccountResponseDto(ReportSummaryByAccountResult result) {
        return ReportSummaryByAccountResponseDto.builder()
                .accountId(result.accountId())
                .accountName(result.accountName())
                .income(result.income())
                .expense(result.expense())
                .net(result.net())
                .count(result.count())
                .build();
    }

    private ReportTimeChartByPeriodResponseDto toTimeChartByPeriodResponseDto(ReportTimeChartByPeriodResult result) {
        return ReportTimeChartByPeriodResponseDto.builder()
                .date(result.getDate())
                .amount(result.getAmount())
                .build();
    }
}

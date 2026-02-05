package com.optifi.domain.transaction.api.mapper;

import com.optifi.domain.category.api.mapper.CategoryMapper;
import com.optifi.domain.shared.TimeHelper;
import com.optifi.domain.shared.UserContext;
import com.optifi.domain.transaction.api.request.GetUserTransactionsRequestDto;
import com.optifi.domain.transaction.api.request.TransactionCreateRequestDto;
import com.optifi.domain.transaction.api.request.TransactionGetSummaryRequestDto;
import com.optifi.domain.transaction.api.request.TransactionUpdateRequestDto;
import com.optifi.domain.transaction.api.response.TransactionDetailsResponseDto;
import com.optifi.domain.transaction.api.response.TransactionGetSummaryResponseDto;
import com.optifi.domain.transaction.api.response.TransactionSummaryResponseDto;
import com.optifi.domain.transaction.application.command.*;
import com.optifi.domain.transaction.application.result.TransactionDetailsResult;
import com.optifi.domain.transaction.application.result.TransactionGetSummaryResult;
import com.optifi.domain.transaction.application.result.TransactionSummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionMapper {
    private final CategoryMapper categoryMapper;
    private final TimeHelper timeHelper;

    public TransactionDetailsResponseDto toDetailsDto(TransactionDetailsResult result, UserContext ctx) {
        return TransactionDetailsResponseDto.builder()
                .id(result.id())
                .amount(result.amount())
                .description(result.description())
                .accountId(result.accountId())
                .category(categoryMapper.toSummaryDto(result.category()))
                .occurredAt(timeHelper.toOffsetDateTime(result.occurredAt(), ctx.zoneId()))
                .createdAt(timeHelper.toOffsetDateTime(result.createdAt(), ctx.zoneId()))
                .updatedAt(timeHelper.toOffsetDateTime(result.updatedAt(), ctx.zoneId()))
                .build();
    }

    public TransactionSummaryResponseDto toSummaryDto(TransactionSummaryResult result, UserContext ctx) {
        return TransactionSummaryResponseDto.builder()
                .id(result.id())
                .accountId(result.accountId())
                .occurredAt(timeHelper.toOffsetDateTime(result.occurredAt(), ctx.zoneId()))
                .amount(result.amount())
                .category(categoryMapper.toSummaryDto(result.category()))
                .build();
    }

    public TransactionGetSummaryResponseDto toGetSummaryDto(TransactionGetSummaryResult result, UserContext ctx) {
        return TransactionGetSummaryResponseDto.builder()
                .accountId(result.accountId())
                .currency(result.currency())
                .from(timeHelper.toLocalDate(result.from(), ctx.zoneId()))
                .to(timeHelper.toLocalDate(result.to(), ctx.zoneId()))
                .income(result.income())
                .expense(result.expense())
                .net(result.net())
                .count(result.count())
                .build();
    }

    public TransactionCreateCommand toCreateCommand(Long accountId, TransactionCreateRequestDto dto, UserContext ctx) {
        return TransactionCreateCommand.builder()
                .userId(ctx.userId())
                .accountId(accountId)
                .amount(dto.amount())
                .description(dto.description())
                .categoryId(dto.categoryId())
                .occurredAt(dto.occurredAt().toInstant())
                .build();
    }

    public TransactionUpdateCommand toUpdateCommand(Long accountId, Long transactionId, TransactionUpdateRequestDto dto, UserContext ctx) {
        return TransactionUpdateCommand.builder()
                .userId(ctx.userId())
                .accountId(accountId)
                .amount(dto.amount())
                .description(dto.description())
                .categoryId(dto.categoryId())
                .id(transactionId)
                .occurredAt(dto.occurredAt().toInstant())
                .build();
    }

    public TransactionGetSummaryCommand toGetSummaryCommand(Long accountId, TransactionGetSummaryRequestDto dto, UserContext ctx) {
        return TransactionGetSummaryCommand.builder()
                .userId(ctx.userId())
                .accountId(accountId)
                .from(dto.from().toInstant())
                .to(dto.to().toInstant())
                .categoryId(dto.categoryId())
                .query(dto.query())
                .build();
    }

    public TransactionQuery toTransactionQuery(Long accountId, GetUserTransactionsRequestDto dto, UserContext ctx) {
        return TransactionQuery.builder()
                .userId(ctx.userId())
                .accountId(accountId)
                .description(dto.description())
                .from(timeHelper.startOfDay(dto.startDate(), ctx.zoneId()))
                .to(timeHelper.startOfNextDay(dto.endDate(), ctx.zoneId()))
                .max(dto.max())
                .min(dto.min())
                .build();
    }

    public TransactionReferenceCommand toReferenceCommand(Long accountId, Long transactionId, UserContext ctx) {
        return TransactionReferenceCommand.builder()
                .userId(ctx.userId())
                .accountId(accountId)
                .transactionId(transactionId)
                .build();
    }
}

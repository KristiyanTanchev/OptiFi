package com.optifi.domain.budget.api.mapper;

import com.optifi.domain.budget.api.request.BudgetEvaluationRequestDto;
import com.optifi.domain.budget.api.request.BudgetSearchRequestDto;
import com.optifi.domain.budget.api.request.BudgetCreateRequestDto;
import com.optifi.domain.budget.api.request.BudgetUpdateRequestDto;
import com.optifi.domain.budget.api.response.BudgetDetailsResponseDto;
import com.optifi.domain.budget.api.response.BudgetEvaluationPerItemResponseDto;
import com.optifi.domain.budget.api.response.BudgetEvaluationResponseDto;
import com.optifi.domain.budget.application.command.BudgetCreateCommand;
import com.optifi.domain.budget.application.command.BudgetEvaluationCommand;
import com.optifi.domain.budget.application.command.BudgetQuery;
import com.optifi.domain.budget.application.command.BudgetUpdateCommand;
import com.optifi.domain.budget.application.result.BudgetDetailsResult;
import com.optifi.domain.budget.application.result.BudgetEvaluationPerItemResult;
import com.optifi.domain.budget.application.result.BudgetEvaluationResult;
import com.optifi.domain.shared.TimeHelper;
import com.optifi.domain.shared.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BudgetMapper {
    private final TimeHelper timeHelper;

    public BudgetCreateCommand toCreateCommand(BudgetCreateRequestDto dto, UserContext ctx) {
        return BudgetCreateCommand.builder()
                .userId(ctx.userId())
                .name(dto.name())
                .amount(dto.amount())
                .budgetPeriod(dto.budgetPeriod())
                .currency(dto.currency())
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .accountIds(dto.accountIds())
                .categoryIds(dto.categoryIds())
                .build();
    }

    public BudgetUpdateCommand toUpdateCommand(Long budgetId, BudgetUpdateRequestDto dto, UserContext ctx) {
        return BudgetUpdateCommand.builder()
                .userId(ctx.userId())
                .budgetId(budgetId)
                .name(dto.name())
                .amount(dto.amount())
                .budgetPeriod(dto.budgetPeriod())
                .currency(dto.currency())
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .build();
    }

    public BudgetQuery toQuery(BudgetSearchRequestDto dto, UserContext ctx) {
        return BudgetQuery.builder()
                .userId(ctx.userId())
                .activeOn(dto.activeOn())
                .archived(dto.archived())
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .build();
    }

    public BudgetDetailsResponseDto toDetailsDto(BudgetDetailsResult result, UserContext ctx) {
        return BudgetDetailsResponseDto.builder()
                .id(result.id())
                .name(result.name())
                .amount(result.amount())
                .currency(result.currency())
                .period(result.period())
                .startDate(result.startDate())
                .endDate(result.endDate())
                .createdAt(timeHelper.toOffsetDateTime(result.createdAt(), ctx.zoneId()))
                .updatedAt(timeHelper.toOffsetDateTime(result.updatedAt(), ctx.zoneId()))
                .archived(result.archived())
                .build();
    }

    public BudgetEvaluationCommand toEvaluationCommand(BudgetEvaluationRequestDto dto, UserContext ctx) {
        return BudgetEvaluationCommand.builder()
                .userId(ctx.userId())
                .from(dto.from())
                .to(dto.to())
                .zoneId(ctx.zoneId())
                .build();
    }

    public BudgetEvaluationResponseDto toEvaluationDto(BudgetEvaluationResult result) {
        return BudgetEvaluationResponseDto.builder()
                .from(result.from())
                .to(result.to())
                .items(result.items().stream().map(this::toEvaluationPerItemResponseDto).toList())
                .build();
    }

    private BudgetEvaluationPerItemResponseDto toEvaluationPerItemResponseDto(BudgetEvaluationPerItemResult result) {
        return BudgetEvaluationPerItemResponseDto.builder()
                .amount(result.amount())
                .currency(result.currency())
                .name(result.name())
                .percentage(result.percentage())
                .spent(result.spent())
                .remaining(result.remaining())
                .id(result.id())
                .startDate(result.startDate())
                .endDate(result.endDate())
                .build();
    }

}

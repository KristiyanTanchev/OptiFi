package com.optifi.domain.category.api.mapper;

import com.optifi.domain.category.api.request.CategoryCreateRequestDto;
import com.optifi.domain.category.api.request.CategoryUpdateRequestDto;
import com.optifi.domain.category.api.response.CategoryDetailsResponseDto;
import com.optifi.domain.category.api.response.CategorySummaryResponseDto;
import com.optifi.domain.category.application.command.CategoryCreateCommand;
import com.optifi.domain.category.application.command.CategoryUpdateCommand;
import com.optifi.domain.category.application.result.CategoryDetailsResult;
import com.optifi.domain.category.application.result.CategorySummaryResult;
import com.optifi.domain.shared.TimeHelper;
import com.optifi.domain.shared.UserContext;
import com.optifi.domain.transaction.api.response.TransactionSummaryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryMapper {
    private final TimeHelper timeHelper;

    public CategoryDetailsResponseDto toDetailsDto(CategoryDetailsResult result, UserContext ctx) {
        return CategoryDetailsResponseDto.builder()
                .id(result.id())
                .name(result.name())
                .description(result.description())
                .icon(result.icon())
                .transactions(result.transactions().stream().map(TransactionSummaryResponseDto::fromResult).toList())
                .createdAt(timeHelper.toOffsetDateTime(result.createdAt(), ctx.zoneId()))
                .updatedAt(timeHelper.toOffsetDateTime(result.updatedAt(), ctx.zoneId()))
                .canEdit(result.canEdit())
                .canDelete(result.canDelete())
                .build();
    }

    public CategorySummaryResponseDto toSummaryDto(CategorySummaryResult result) {
        return CategorySummaryResponseDto.builder()
                .id(result.id())
                .name(result.name())
                .icon(result.icon())
                .canEdit(result.canEdit())
                .canDelete(result.canDelete())
                .build();
    }

    public CategoryCreateCommand toCreateCommand(CategoryCreateRequestDto dto, UserContext ctx) {
        return CategoryCreateCommand.builder()
                .userId(ctx.userId())
                .name(dto.name())
                .description(dto.description())
                .icon(dto.icon())
                .build();
    }

    public CategoryUpdateCommand toUpdateCommand(long categoryId, CategoryUpdateRequestDto dto, UserContext ctx) {
        return CategoryUpdateCommand.builder()
                .userId(ctx.userId())
                .categoryId(categoryId)
                .name(dto.name())
                .description(dto.description())
                .icon(dto.icon())
                .build();
    }
}

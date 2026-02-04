package com.optifi.domain.account.api.mapper;

import com.optifi.domain.account.api.request.AccountCreateRequestDto;
import com.optifi.domain.account.api.request.AccountUpdateRequestDto;
import com.optifi.domain.account.api.response.AccountDetailsResponseDto;
import com.optifi.domain.account.api.response.AccountSummaryResponseDto;
import com.optifi.domain.account.application.command.AccountCreateCommand;
import com.optifi.domain.account.application.command.AccountUpdateCommand;
import com.optifi.domain.account.application.result.AccountDetailsResult;
import com.optifi.domain.account.application.result.AccountSummaryResult;
import com.optifi.domain.shared.TimeHelper;
import com.optifi.domain.shared.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountMapper {
    private final TimeHelper timeHelper;

    public AccountDetailsResponseDto toDetailsDto(AccountDetailsResult result, UserContext ctx) {
        return AccountDetailsResponseDto.builder()
                .id(result.id())
                .name(result.name())
                .type(result.type())
                .currency(result.currency())
                .institution(result.institution())
                .archived(result.archived())
                .createdAt(timeHelper.toOffsetDateTime(result.createdAt(), ctx.zoneId()))
                .updatedAt(timeHelper.toOffsetDateTime(result.updatedAt(), ctx.zoneId()))
                .build();
    }

    public AccountSummaryResponseDto toSummaryDto(AccountSummaryResult result) {
        return AccountSummaryResponseDto.builder()
                .id(result.id())
                .name(result.name())
                .type(result.type())
                .currency(result.currency())
                .institution(result.institution())
                .archived(result.archived())
                .build();
    }

    public AccountCreateCommand toCreateCommand(AccountCreateRequestDto dto, UserContext ctx) {
        return AccountCreateCommand.builder()
                .userId(ctx.userId())
                .name(dto.name())
                .type(dto.type())
                .currency(dto.currency())
                .institution(dto.institution())
                .build();
    }

    public AccountUpdateCommand toUpdateCommand(Long accountId, AccountUpdateRequestDto dto, UserContext ctx) {
        return AccountUpdateCommand.builder()
                .userId(ctx.userId())
                .accountId(accountId)
                .name(dto.name())
                .type(dto.type())
                .currency(dto.currency())
                .institution(dto.institution())
                .build();
    }
}

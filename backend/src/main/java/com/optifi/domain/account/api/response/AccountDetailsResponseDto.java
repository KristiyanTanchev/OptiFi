package com.optifi.domain.account.api.response;

import com.optifi.domain.account.application.result.AccountDetailsResult;
import com.optifi.domain.shared.AccountType;
import com.optifi.domain.shared.Currency;
import com.optifi.domain.transaction.api.response.TransactionSummaryResponseDto;

import java.time.Instant;
import java.util.List;

public record AccountDetailsResponseDto(
        long id,
        String name,
        AccountType type,
        Currency currency,
        String institution,
        List<TransactionSummaryResponseDto> transactions,
        boolean archived,
        Instant createdAt,
        Instant updatedAt
) {
    public static AccountDetailsResponseDto fromResult(AccountDetailsResult result) {
        return new AccountDetailsResponseDto(
                result.id(),
                result.name(),
                result.type(),
                result.currency(),
                result.institution(),
                result.transactions().stream().map(TransactionSummaryResponseDto::fromResult).toList(),
                result.archived(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}

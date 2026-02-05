package com.optifi.domain.account.application.result;

import com.optifi.domain.account.model.Account;
import com.optifi.domain.shared.AccountType;
import com.optifi.domain.shared.Currency;
import lombok.Builder;

import java.time.Instant;

@Builder
public record AccountDetailsResult(
        long id,
        String name,
        AccountType type,
        Currency currency,
        String institution,
        boolean archived,
        Instant createdAt,
        Instant updatedAt
) {
    public static AccountDetailsResult fromEntity(Account account) {
        return AccountDetailsResult.builder()
                .id(account.getId())
                .name(account.getName())
                .type(account.getType())
                .currency(account.getCurrency())
                .institution(account.getInstitution())
                .archived(account.isArchived())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}

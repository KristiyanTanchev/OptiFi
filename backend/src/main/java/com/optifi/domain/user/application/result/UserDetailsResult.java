package com.optifi.domain.user.application.result;

import com.optifi.domain.account.application.result.AccountSummaryResult;
import com.optifi.domain.user.model.User;

import java.util.List;

public record UserDetailsResult(
        long id,
        String username,
        String role,
        String email,
        List<AccountSummaryResult> accounts,
        String baseCurrency,
        String locale
) {
    public static UserDetailsResult fromEntity(User user) {
        return new UserDetailsResult(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getEmail(),
                user.getAccounts().stream().map(AccountSummaryResult::fromEntity).toList(),
                user.getBaseCurrency().name(),
                user.getLocale().tag()
        );
    }
}

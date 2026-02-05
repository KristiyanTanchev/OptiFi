package com.optifi.domain.user.application.result;

import com.optifi.domain.account.application.result.AccountSummaryResult;
import com.optifi.domain.shared.Currency;
import com.optifi.domain.shared.Role;
import com.optifi.domain.shared.SupportedLocale;
import com.optifi.domain.user.model.User;

import java.util.List;

public record UserDetailsResult(
        long id,
        String username,
        Role role,
        String email,
        List<AccountSummaryResult> accounts,
        Currency baseCurrency,
        SupportedLocale locale,
        String timezone
) {
    public static UserDetailsResult fromEntity(User user) {
        return new UserDetailsResult(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getEmail(),
                user.getAccounts().stream().map(AccountSummaryResult::fromEntity).toList(),
                user.getBaseCurrency(),
                user.getLocale(),
                user.getTimeZoneId()
        );
    }
}

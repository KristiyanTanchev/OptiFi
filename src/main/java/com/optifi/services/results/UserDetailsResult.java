package com.optifi.services.results;

import com.optifi.models.Currency;
import com.optifi.models.Role;
import com.optifi.models.User;

import java.util.List;

public record UserDetailsResult(
        long id,
        String username,
        Role role,
        String email,
        List<AccountSummaryResult> accounts,
        Currency baseCurrency,
        String locale
) {
    public static UserDetailsResult fromEntity(User user) {
        return new UserDetailsResult(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getEmail(),
                user.getAccounts().stream().map(AccountSummaryResult::fromEntity).toList(),
                user.getBaseCurrency(),
                user.getLocale().tag()
        );
    }
}

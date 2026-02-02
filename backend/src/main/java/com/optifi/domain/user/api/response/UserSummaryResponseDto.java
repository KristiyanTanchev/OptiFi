package com.optifi.domain.user.api.response;

import com.optifi.domain.shared.Role;
import com.optifi.domain.user.application.result.UserSummaryResult;

public record UserSummaryResponseDto(
        long id,
        String username,
        Role role
) {
    public static UserSummaryResponseDto fromResult(UserSummaryResult result) {
        return new UserSummaryResponseDto(
                result.id(),
                result.username(),
                result.role()
        );
    }
}

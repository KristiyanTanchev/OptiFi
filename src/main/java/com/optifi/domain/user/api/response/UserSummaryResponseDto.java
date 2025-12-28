package com.optifi.domain.user.api.response;

import com.optifi.domain.user.application.result.UserSummaryResult;

public record UserSummaryResponseDto(
        long id,
        String username,
        String role
) {
    public static UserSummaryResponseDto fromResult(UserSummaryResult result) {
        return new UserSummaryResponseDto(
                result.id(),
                result.username(),
                result.role()
        );
    }
}

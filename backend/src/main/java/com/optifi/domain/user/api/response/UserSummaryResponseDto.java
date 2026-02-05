package com.optifi.domain.user.api.response;

import com.optifi.domain.shared.Role;
import lombok.Builder;

@Builder
public record UserSummaryResponseDto(
        long id,
        String username,
        Role role
) {
}

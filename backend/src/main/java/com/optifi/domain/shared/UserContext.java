package com.optifi.domain.shared;

import com.optifi.security.CustomUserDetails;

import java.time.ZoneId;

public record UserContext(
        long userId,
        ZoneId zoneId,
        Currency currency
) {
    public static UserContext from(CustomUserDetails principal) {
        return new UserContext(principal.getId(), principal.getZoneId(), principal.getCurrency());
    }
}

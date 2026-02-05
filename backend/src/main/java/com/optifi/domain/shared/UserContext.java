package com.optifi.domain.shared;

import lombok.Builder;

import java.time.ZoneId;

@Builder
public record UserContext(
        long userId,
        ZoneId zoneId,
        Currency currency
) {
}

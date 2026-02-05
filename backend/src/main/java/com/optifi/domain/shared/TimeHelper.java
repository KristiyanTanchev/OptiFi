package com.optifi.domain.shared;

import org.springframework.stereotype.Component;

import java.time.*;

@Component
public class TimeHelper {
    private final ZoneId defaultZoneId = ZoneId.of("Europe/Sofia");

    public LocalDate toLocalDate(Instant instant, ZoneId zone) {
        if (zone == null) {
            zone = defaultZoneId;
        }
        if (instant == null) {
            return null;
        }
        return instant.atZone(zone).toLocalDate();
    }

    public OffsetDateTime toOffsetDateTime(Instant instant, ZoneId zone) {
        if (zone == null) {
            zone = defaultZoneId;
        }
        if (instant == null) {
            return null;
        }
        return instant.atZone(zone).toOffsetDateTime();
    }

    public Instant startOfDay(LocalDate date, ZoneId zone) {
        if (date == null) {
            return null;
        }
        if (zone == null) {
            zone = defaultZoneId;
        }
        return date.atStartOfDay(zone).toInstant();
    }

    public Instant startOfNextDay(LocalDate date, ZoneId zone) {
        if (date == null) {
            return null;
        }
        if (zone == null) {
            zone = defaultZoneId;
        }
        return date.plusDays(1).atStartOfDay(zone).toInstant();
    }
}

package com.optifi.domain.shared;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class TimeHelper {

    public LocalDate toLocalDate(Instant instant, ZoneId zone) {
        if (zone == null) {
            zone = ZoneId.systemDefault();
        }
        return instant.atZone(zone).toLocalDate();
    }

    public Instant startOfDay(LocalDate date, ZoneId zone) {
        if (date == null) {
            return null;
        }
        if (zone == null) {
            zone = ZoneId.systemDefault();
        }
        return date.atStartOfDay(zone).toInstant();
    }

    public Instant startOfNextDay(LocalDate date, ZoneId zone) {
        if (date == null) {
            return null;
        }
        if (zone == null) {
            zone = ZoneId.systemDefault();
        }
        return date.plusDays(1).atStartOfDay(zone).toInstant();
    }
}

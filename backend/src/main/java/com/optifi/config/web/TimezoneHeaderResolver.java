package com.optifi.config.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.ZoneId;

@Slf4j
@Component
public class TimezoneHeaderResolver {
    public ZoneId resolve(String headerValue, ZoneId fallback) {
        if (headerValue == null || headerValue.isBlank()) {
            return fallback;
        }

        String tz = headerValue.trim();

        try {
            return ZoneId.of(tz);
        } catch (DateTimeException ex) {
            log.debug("Invalid timezone header: {}", tz);
            return fallback;
        }
    }
}

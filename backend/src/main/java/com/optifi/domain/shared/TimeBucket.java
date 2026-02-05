package com.optifi.domain.shared;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.optifi.exceptions.EnumParsingError;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;

public enum TimeBucket {
    DAY("day"),
    WEEK("week"),
    MONTH("month"),
    YEAR("year");

    private final String value;

    TimeBucket(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static TimeBucket from(String v) {
        if (v == null || v.isBlank()) {
            throw new EnumParsingError("bucket", "bucket is required");
        }
        String s = v.trim();

        return Arrays.stream(values())
                .filter(x -> x.value.equalsIgnoreCase(s) || x.name().equalsIgnoreCase(s))
                .findFirst()
                .orElseThrow(() -> new EnumParsingError("bucket", "Unsupported bucket: " + v));
    }
}

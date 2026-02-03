package com.optifi.domain.shared;

import java.time.temporal.ChronoUnit;

public enum TimeBucket {
    DAY,
    WEEK,
    MONTH,
    YEAR;

    public ChronoUnit toChronoUnit() {
        return switch (this) {
            case DAY -> ChronoUnit.DAYS;
            case WEEK -> ChronoUnit.WEEKS;
            case MONTH -> ChronoUnit.MONTHS;
            case YEAR -> ChronoUnit.YEARS;
        };
    }
}

package com.optifi.domain.shared;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.optifi.exceptions.EnumParsingError;

import java.util.Arrays;

public enum SupportedLocale {
    BG_BG("bg-BG"),
    EN_US("en-US");

    private final String value;

    SupportedLocale(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static SupportedLocale from(String v) {
        return Arrays.stream(values())
                .filter(x -> x.value.equalsIgnoreCase(v))
                .findFirst()
                .orElseThrow(() -> new EnumParsingError("locale", "Unsupported locale: " + v));
    }
}

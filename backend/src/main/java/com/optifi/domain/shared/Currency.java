package com.optifi.domain.shared;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.optifi.exceptions.EnumParsingError;

import java.util.Arrays;

public enum Currency {
    USD("usd"),
    EUR("eur");

    private final String value;

    Currency(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static Currency from(String v) {
        if (v == null || v.isBlank()) {
            throw new EnumParsingError("currency", "currency is required");
        }
        String s = v.trim();

        return Arrays.stream(values())
                .filter(x -> x.value.equalsIgnoreCase(s) || x.name().equalsIgnoreCase(s))
                .findFirst()
                .orElseThrow(() -> new EnumParsingError("currency", "Unsupported currency: " + v));
    }
}

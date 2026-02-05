package com.optifi.domain.shared;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.optifi.exceptions.EnumParsingError;

import java.util.Arrays;

public enum AccountType {
    CASH("cash"),
    BANK("bank");

    private final String value;

    AccountType(String value) { this.value = value; }

    @JsonValue
    public String value() { return value; }

    @JsonCreator
    public static AccountType from(String v) {
        return Arrays.stream(values())
                .filter(x -> x.value.equalsIgnoreCase(v))
                .findFirst()
                .orElseThrow(() -> new EnumParsingError("accountType", "Unsupported accountType: " + v));
    }
}
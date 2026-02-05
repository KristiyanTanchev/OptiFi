package com.optifi.domain.shared;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.optifi.exceptions.EnumParsingError;

import java.util.Arrays;

public enum TransactionType {
    INCOME("income"),
    EXPENSE("expense"),
    ANY("any");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static TransactionType from(String v) {
        return Arrays.stream(values())
                .filter(x -> x.value.equalsIgnoreCase(v))
                .findFirst()
                .orElseThrow(() -> new EnumParsingError("transactionType", "Unsupported transactionType: " + v));
    }
}

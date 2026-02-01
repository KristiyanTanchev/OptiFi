package com.optifi.domain.reporting.application.command;

import com.optifi.domain.shared.model.TransactionType;
import com.optifi.exceptions.EnumParsingError;

import java.time.Instant;

public record ReportCategoriesCommand(
        Long userId,
        Instant from,
        Instant to,
        TransactionType type,
        Integer limit
) {
    public static ReportCategoriesCommand from(long userId, Instant from, Instant to, String type, Integer limit) {
        TransactionType transactionType = TransactionType.ANY;
        if (type != null) {
            try {
                transactionType = TransactionType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new EnumParsingError("transactionType", "Unsupported transaction type: " + type);
            }
        }
        return new ReportCategoriesCommand(
                userId,
                from,
                to,
                transactionType,
                limit
        );
    }
}

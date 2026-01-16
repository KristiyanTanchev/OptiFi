package com.optifi.domain.transaction.application.command;

public record TransactionReferenceCommand(
        Long userId,
        Long accountId,
        Long transactionId
) {
}

package com.optifi.domain.transaction.application.command;

import lombok.Builder;

@Builder
public record TransactionReferenceCommand(
        Long userId,
        Long accountId,
        Long transactionId
) {
}

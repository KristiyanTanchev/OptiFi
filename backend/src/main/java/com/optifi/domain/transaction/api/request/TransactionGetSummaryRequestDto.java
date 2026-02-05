package com.optifi.domain.transaction.api.request;

import java.time.OffsetDateTime;

public record TransactionGetSummaryRequestDto(
        OffsetDateTime from,
        OffsetDateTime to,
        Long categoryId,
        String query
) {
}

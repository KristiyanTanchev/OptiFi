export type TransactionGetSummaryRequest = {
    from?: string;      // Instant ISO string
    to?: string;        // Instant ISO string
    categoryId?: number;
    query?: string;
};

export type TransactionGetSummaryResponse = {
    accountId: number;
    currency: string;
    from: string;
    to: string;
    income: string;   // BigDecimal (safe as string)
    expense: string;
    net: string;
    count: number;
};

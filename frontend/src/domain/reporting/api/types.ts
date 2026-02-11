export type Moneyish = string | number;

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
    income: Moneyish;
    expense: Moneyish;
    net: string;
    count: number;
};

export type ReportSummaryRequest = {
    from?: string;
    to?: string;
    currency: string; // required
};

export type ReportSummaryByAccount = {
    accountId: number;
    accountName: string;
    income: Moneyish;
    expense: Moneyish;
    net: Moneyish;
    count: number;
};

export type ReportSummaryResponse = {
    currency: string;
    income: Moneyish;
    expense: Moneyish;
    net: Moneyish;
    count: number;
    byAccount: ReportSummaryByAccount[];
};

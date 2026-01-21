export type Moneyish = string | number;

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

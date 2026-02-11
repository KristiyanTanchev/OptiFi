import {useQuery} from "@tanstack/react-query";
import {getTransactionsSummary} from "../api/reportsApi.ts";
import type {TransactionGetSummaryRequest} from "../api/types.ts";
import type {ReportSummaryRequest} from "../api/types.ts";
import {getReportSummary} from "../api/reportsApi.ts";

export function useTransactionsSummary(
    accountId: number | undefined,
    req: TransactionGetSummaryRequest
) {
    return useQuery({
        queryKey: ["transactions", "summary", accountId, req],
        queryFn: () => {
            if (!accountId) throw new Error("accountId is required");
            return getTransactionsSummary(accountId, req);
        },
        enabled: !!accountId,
        staleTime: 30_000,
    });
}

export function useReportSummary(req: ReportSummaryRequest) {
    return useQuery({
        queryKey: ["reports", "summary", req],
        queryFn: () => getReportSummary(req),
        enabled: !!req.currency,
        staleTime: 30_000,
    });
}

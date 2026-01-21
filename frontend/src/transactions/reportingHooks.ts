import {useQuery} from "@tanstack/react-query";
import {getTransactionsSummary} from "./reportingApi";
import type {TransactionGetSummaryRequest} from "./reportingTypes";

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

import { useQuery } from "@tanstack/react-query";
import { transactionsApi } from "../api/transactionsApi";
import type {TransactionFilters} from "../types/transaction";

export function useTransactions(
    accountId: number,
    filters?: TransactionFilters,
    page = 0
) {
    return useQuery({
        queryKey: ["transactions", accountId, filters, page],
        queryFn: () =>
            transactionsApi.getAll(accountId, filters, page),
    });
}

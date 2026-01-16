import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { transactionsApi } from "../api/transactionsApi";
import type {TransactionCreateRequest, TransactionUpdateRequest, TransactionFilters} from "../types/transaction";

export function useTransactions(
    accountId: number,
    filters?: TransactionFilters,
    page = 0
) {
    return useQuery({
        queryKey: ["transactions", accountId, filters, page],
        queryFn: () =>
            transactionsApi.getAll(accountId, filters, page),
        enabled: Number.isFinite(accountId),
    });
}

export function useCreateTransaction(accountId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (data: TransactionCreateRequest) =>
            transactionsApi.create(accountId, data),
        onSuccess: async () => {
            await qc.invalidateQueries({ queryKey: ["transactions", accountId] });
            await qc.invalidateQueries({ queryKey: ["account", accountId] });
        },
    });
}

export function useTransactionDetails(accountId: number, transactionId: number | null) {
    return useQuery({
        queryKey: ["transaction", accountId, transactionId],
        queryFn: () => transactionsApi.getById(accountId, transactionId as number),
        enabled: Number.isFinite(accountId) && transactionId != null,
    });
}

export function useUpdateTransaction(accountId: number, transactionId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (data: TransactionUpdateRequest) =>
            transactionsApi.update(accountId, transactionId, data),
        onSuccess: async () => {
            await qc.invalidateQueries({ queryKey: ["transactions", accountId] });
            await qc.invalidateQueries({ queryKey: ["account", accountId] });
            await qc.invalidateQueries({ queryKey: ["transaction", accountId, transactionId] });
        },
    });
}

export function useDeleteTransaction(accountId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (transactionId: number) =>
            transactionsApi.delete(accountId, transactionId),
        onSuccess: () =>
            qc.invalidateQueries({ queryKey: ["transactions", accountId] }),
    });
}

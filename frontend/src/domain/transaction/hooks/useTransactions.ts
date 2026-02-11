import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {transactionsApi} from "../api/transactionsApi.ts";
import type {
    TransactionCreateRequest,
    TransactionUpdateRequest,
    TransactionFilters,
} from "../api/types.ts";

type EnabledOpts = { enabled?: boolean };

export function useTransactions(accountId: number, filters?: TransactionFilters, page = 0) {
    return useQuery({
        queryKey: ["transactions", accountId, filters, page],
        queryFn: () => transactionsApi.getAll(accountId, filters, page),
        enabled: Number.isFinite(accountId),
    });
}

export function useCreateTransaction(accountId: number, opts?: EnabledOpts) {
    const qc = useQueryClient();
    const enabled = opts?.enabled ?? true;

    return useMutation({
        mutationFn: async (data: TransactionCreateRequest) => {
            if (!enabled) throw new Error("CreateTransaction is disabled");
            return transactionsApi.create(accountId, data);
        },
        onSuccess: async () => {
            await qc.invalidateQueries({queryKey: ["transactions", accountId]});
            await qc.invalidateQueries({queryKey: ["account", accountId]});
        },
    });
}

export function useTransactionDetails(accountId: number, transactionId: number | null, opts?: EnabledOpts) {
    const enabled = opts?.enabled ?? true;

    return useQuery({
        queryKey: ["transaction", accountId, transactionId],
        queryFn: () => transactionsApi.getById(accountId, transactionId as number),
        enabled: enabled && Number.isFinite(accountId) && transactionId != null,
    });
}

export function useUpdateTransaction(
    accountId: number,
    transactionId: number | null,
    opts?: EnabledOpts
) {
    const qc = useQueryClient();
    const enabled = opts?.enabled ?? true;

    return useMutation({
        mutationFn: async (data: TransactionUpdateRequest) => {
            if (!enabled) throw new Error("UpdateTransaction is disabled");
            if (transactionId == null) throw new Error("transactionId is required to update transaction");
            return transactionsApi.update(accountId, transactionId, data);
        },
        onSuccess: async () => {
            await qc.invalidateQueries({queryKey: ["transactions", accountId]});
            await qc.invalidateQueries({queryKey: ["account", accountId]});
            if (transactionId != null) {
                await qc.invalidateQueries({queryKey: ["transaction", accountId, transactionId]});
            }
        },
    });
}

export function useDeleteTransaction(accountId: number, opts?: EnabledOpts) {
    const qc = useQueryClient();
    const enabled = opts?.enabled ?? true;

    return useMutation({
        mutationFn: async (transactionId: number) => {
            if (!enabled) throw new Error("DeleteTransaction is disabled");
            return transactionsApi.delete(accountId, transactionId);
        },
        onSuccess: async () => {
            await qc.invalidateQueries({queryKey: ["transactions", accountId]});
            await qc.invalidateQueries({queryKey: ["account", accountId]});
        },
    });
}

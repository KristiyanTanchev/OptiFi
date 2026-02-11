import {useQuery, useMutation, useQueryClient} from "@tanstack/react-query";
import {accountsApi} from "../api/accountsApi.ts";
import type {AccountUpdateRequest} from "../api/types.ts";

export function useAccounts() {
    return useQuery({
        queryKey: ["accounts"],
        queryFn: accountsApi.getAll,
    });
}

export function useAccount(id: number) {
    return useQuery({
        queryKey: ["account", id],
        queryFn: () => accountsApi.getById(id),
        enabled: Number.isFinite(id),
    });
}

export function useCreateAccount() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: accountsApi.create,
        onSuccess: () => qc.invalidateQueries({queryKey: ["accounts"]}),
    });
}

export function useUpdateAccount(id: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (data: AccountUpdateRequest) => accountsApi.update(id, data),
        onSuccess: async () => {
            await qc.invalidateQueries({queryKey: ["accounts"]});
            await qc.invalidateQueries({queryKey: ["account", id]});
        },
    });
}

export function useArchiveAccount(id: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: () => accountsApi.archive(id),
        onSuccess: async () => {
            await qc.invalidateQueries({queryKey: ["accounts"]});
            await qc.invalidateQueries({queryKey: ["account", id]});
        },
    });
}

export function useUnarchiveAccount(id: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: () => accountsApi.unarchive(id),
        onSuccess: async () => {
            await qc.invalidateQueries({queryKey: ["accounts"]});
            await qc.invalidateQueries({queryKey: ["account", id]});
        },
    });
}

export function useDeleteAccount() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => accountsApi.delete(id),
        onSuccess: () => qc.invalidateQueries({queryKey: ["accounts"]}),
    });
}

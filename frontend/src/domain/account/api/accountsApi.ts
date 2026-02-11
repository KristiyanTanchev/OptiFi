import {http} from "@shared/api/http.ts";
import type {
    AccountSummary,
    AccountDetails,
    AccountCreateRequest,
    AccountUpdateRequest,
} from "./types.ts";

export const accountsApi = {
    getAll(): Promise<AccountSummary[]> {
        return http.get("/api/accounts").then(r => r.data);
    },

    getById(id: number): Promise<AccountDetails> {
        return http.get(`/api/accounts/${id}`).then(r => r.data);
    },

    create(data: AccountCreateRequest): Promise<AccountDetails> {
        return http.post("/api/accounts", data).then(r => r.data);
    },

    update(id: number, data: AccountUpdateRequest): Promise<void> {
        return http.put(`/api/accounts/${id}`, data).then(() => {
        });
    },

    archive(id: number): Promise<void> {
        return http.put(`/api/accounts/${id}/archive`).then(() => {
        });
    },

    unarchive(id: number): Promise<void> {
        return http.put(`/api/accounts/${id}/unarchive`).then(() => {
        });
    },

    delete(id: number): Promise<void> {
        return http.delete(`/api/accounts/${id}`).then(() => {
        });
    },
};

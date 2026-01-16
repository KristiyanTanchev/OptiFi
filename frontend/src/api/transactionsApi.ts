import { http } from "./http";
import type {
    TransactionSummary,
    TransactionDetails,
    TransactionCreateRequest,
    TransactionUpdateRequest,
    TransactionFilters,
} from "../types/transaction";
import type {Page} from "../types/page";

export const transactionsApi = {
    getAll(
        accountId: number,
        filters?: TransactionFilters,
        page = 0,
        size = 20
    ): Promise<Page<TransactionSummary>> {
        return http.get(
            `/api/accounts/${accountId}/transactions`,
            { params: { ...filters, page, size } }
        ).then(r => r.data);
    },

    getById(accountId: number, transactionId: number): Promise<TransactionDetails> {
        return http
            .get(`/api/accounts/${accountId}/transactions/${transactionId}`)
            .then(r => r.data);
    },

    create(
        accountId: number,
        data: TransactionCreateRequest
    ): Promise<TransactionDetails> {
        return http
            .post(`/api/accounts/${accountId}/transactions`, data)
            .then(r => r.data);
    },

    update(
        accountId: number,
        transactionId: number,
        data: TransactionUpdateRequest
    ): Promise<void> {
        return http
            .put(`/api/accounts/${accountId}/transactions/${transactionId}`, data)
            .then(() => {});
    },

    delete(accountId: number, transactionId: number): Promise<void> {
        return http
            .delete(`/api/accounts/${accountId}/transactions/${transactionId}`)
            .then(() => {});
    },
};

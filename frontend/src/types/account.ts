import type {TransactionSummary} from "./transaction.ts";

export type AccountSummary = {
    id: number;
    name: string;
    type: string;
    currency: string;
    institution?: string;
    archived: boolean;
};

export type AccountDetails = AccountSummary & {
    transactions: TransactionSummary[];
    createdAt: string;
    updatedAt?: string;
};

export type AccountCreateRequest = {
    name: string;
    type: string;
    currency: string;
    institution?: string;
};

export type AccountUpdateRequest = AccountCreateRequest;

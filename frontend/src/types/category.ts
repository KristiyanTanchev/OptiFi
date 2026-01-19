import type {TransactionSummary} from "./transaction";

export type CategorySummary = {
    id: number;
    name: string;
    icon: string;
    canEdit: boolean;
    canDelete: boolean;
};

export type CategoryDetails = CategorySummary & {
    description: string;
    transactions: TransactionSummary[];
    createdAt: string;
    updatedAt?: string;
};

export type CategoryCreateRequest = {
    name: string;
    description: string;
    icon: string;
};

export type CategoryUpdateRequest = CategoryCreateRequest;

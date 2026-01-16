export type CategorySummary = {
    id: number;
    name: string;
};

export type TransactionSummary = {
    id: number;
    accountId: number;
    occurredAt: string;
    amount: string; // BigDecimal → string
    category: CategorySummary;
};

export type TransactionDetails = TransactionSummary & {
    description?: string;
    createdAt: string;
    updatedAt?: string;
};

export type TransactionCreateRequest = {
    amount: string;
    occurredAt: string;
    description?: string;
    categoryId: number;
};

export type TransactionUpdateRequest = TransactionCreateRequest;

export type TransactionFilters = {
    startDate?: string;
    endDate?: string;
    min?: string;
    max?: string;
    description?: string;
};

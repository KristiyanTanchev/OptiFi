import {http} from "../api/http";
import {apiPath} from "../api/apiPath";
import type {
    TransactionGetSummaryRequest,
    TransactionGetSummaryResponse,
} from "./reportingTypes";

function cleanParams(params: Record<string, unknown>) {
    return Object.fromEntries(
        Object.entries(params).filter(([, v]) => v !== undefined && v !== null && v !== "")
    );
}

export async function getTransactionsSummary(
    accountId: number,
    req: TransactionGetSummaryRequest
): Promise<TransactionGetSummaryResponse> {
    const params = cleanParams({
        from: req.from,
        to: req.to,
        categoryId: req.categoryId,
        query: req.query,
    });

    const {data} = await http.get<TransactionGetSummaryResponse>(
        apiPath(`/accounts/${accountId}/transactions/summary`),
        {params}
    );

    return data;
}

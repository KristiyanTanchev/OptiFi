import {http} from "@shared/api/http.ts";
import {apiPath} from "@shared/api/apiPath.ts";
import type {
    TransactionGetSummaryRequest,
    TransactionGetSummaryResponse,
    ReportSummaryRequest,
    ReportSummaryResponse
} from "./types.ts";

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

export async function getReportSummary(req: ReportSummaryRequest): Promise<ReportSummaryResponse> {
    const params = cleanParams({
        from: req.from,
        to: req.to,
        currency: req.currency,
    });

    const {data} = await http.get<ReportSummaryResponse>(
        apiPath("/reports/summary"),
        {params}
    );

    return data;
}

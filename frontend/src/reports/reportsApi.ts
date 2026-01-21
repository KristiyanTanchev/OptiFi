import {http} from "../api/http";
import {apiPath} from "../api/apiPath";
import type {ReportSummaryRequest, ReportSummaryResponse} from "./reportsTypes";

function cleanParams(params: Record<string, unknown>) {
    return Object.fromEntries(
        Object.entries(params).filter(([, v]) => v !== undefined && v !== null && v !== "")
    );
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

import {useQuery} from "@tanstack/react-query";
import {getReportSummary} from "./reportsApi";
import type {ReportSummaryRequest} from "./reportsTypes";

export function useReportSummary(req: ReportSummaryRequest) {
    return useQuery({
        queryKey: ["reports", "summary", req],
        queryFn: () => getReportSummary(req),
        enabled: !!req.currency,
        staleTime: 30_000,
    });
}

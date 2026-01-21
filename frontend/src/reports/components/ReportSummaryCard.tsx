import {Alert, Card, CardContent, CircularProgress, Stack, Typography} from "@mui/material";
import {useMemo} from "react";
import {useReportSummary} from "../reportsHooks";
import type {Moneyish, ReportSummaryRequest} from "../reportsTypes";

function money(value: Moneyish, currency: string) {
    const n = typeof value === "number" ? value : Number(value);
    if (!Number.isFinite(n)) return `${value} ${currency}`;
    return new Intl.NumberFormat(undefined, {style: "currency", currency}).format(n);
}

export function ReportSummaryCard(props: { request: ReportSummaryRequest }) {
    // keep stable identity to avoid noisy refetching
    const req = useMemo(
        () => ({
            currency: props.request.currency,
            from: props.request.from || undefined,
            to: props.request.to || undefined,
        }),
        [props.request.currency, props.request.from, props.request.to]
    );

    const q = useReportSummary(req);

    return (
        <Card variant="outlined">
            <CardContent>
                <Typography variant="subtitle2" sx={{mb: 1.5}}>
                    Global report summary
                </Typography>

                {q.isLoading && <CircularProgress size={22}/>}

                {q.isError && <Alert severity="error">Failed to load report summary.</Alert>}

                {q.data && (
                    <Stack direction="row" spacing={3} flexWrap="wrap">
                        <Stack>
                            <Typography variant="caption" color="text.secondary">Income</Typography>
                            <Typography variant="h6">{money(q.data.income, q.data.currency)}</Typography>
                        </Stack>

                        <Stack>
                            <Typography variant="caption" color="text.secondary">Expense</Typography>
                            <Typography variant="h6">{money(q.data.expense, q.data.currency)}</Typography>
                        </Stack>

                        <Stack>
                            <Typography variant="caption" color="text.secondary">Net</Typography>
                            <Typography variant="h6">{money(q.data.net, q.data.currency)}</Typography>
                        </Stack>

                        <Stack>
                            <Typography variant="caption" color="text.secondary">Count</Typography>
                            <Typography variant="h6">{q.data.count}</Typography>
                        </Stack>
                    </Stack>
                )}
            </CardContent>
        </Card>
    );
}

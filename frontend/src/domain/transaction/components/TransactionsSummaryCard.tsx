import {Alert, Card, CardContent, CircularProgress, Stack, Typography} from "@mui/material";
import {useTransactionsSummary} from "@reporting/hooks/reportsHooks.ts";
import type {Moneyish, TransactionGetSummaryRequest} from "@reporting/api/types.ts";

function money(value: Moneyish, currency: string) {
    const n = Number(value);
    if (!Number.isFinite(n)) return `${value} ${currency}`;
    return new Intl.NumberFormat(undefined, {style: "currency", currency}).format(n);
}

export function TransactionsSummaryCard(props: {
    accountId: number;
    filters: TransactionGetSummaryRequest;
}) {
    const q = useTransactionsSummary(props.accountId, props.filters);

    return (
        <Card variant="outlined">
            <CardContent>
                <Typography variant="subtitle2" sx={{mb: 1.5}}>
                    Account transactions summary
                </Typography>

                {q.isLoading && <CircularProgress size={22}/>}

                {q.isError && <Alert severity="error">Failed to load transaction summary.</Alert>}

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

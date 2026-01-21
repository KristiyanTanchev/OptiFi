import {useMemo, useState} from "react";
import {Box, MenuItem, Stack, TextField, Typography} from "@mui/material";
import {ReportSummaryCard} from "../reports/components/ReportSummaryCard";
import {ReportSummaryByAccountTable} from "../reports/components/ReportSummaryByAccountTable";
import {useReportSummary} from "../reports/reportsHooks";

export default function ReportsPage() {
    const [currency, setCurrency] = useState("EUR");
    const [from, setFrom] = useState("");
    const [to, setTo] = useState("");

    const req = useMemo(
        () => ({
            currency,
            from: from || undefined,
            to: to || undefined,
        }),
        [currency, from, to]
    );

    const q = useReportSummary(req);

    return (
        <Box>
            <Typography variant="h5" sx={{mb: 2}}>
                Reports
            </Typography>

            <Stack direction="row" spacing={2} sx={{mb: 2}} flexWrap="wrap">
                <TextField
                    select
                    label="Currency"
                    value={currency}
                    onChange={(e) => setCurrency(e.target.value)}
                    sx={{minWidth: 140}}
                >
                    <MenuItem value="EUR">EUR</MenuItem>
                    <MenuItem value="USD">USD</MenuItem>
                </TextField>

                <TextField
                    label="From (ISO Instant)"
                    value={from}
                    onChange={(e) => setFrom(e.target.value)}
                    placeholder="2026-01-01T00:00:00Z"
                    sx={{minWidth: 260}}
                />

                <TextField
                    label="To (ISO Instant)"
                    value={to}
                    onChange={(e) => setTo(e.target.value)}
                    placeholder="2026-01-31T23:59:59Z"
                    sx={{minWidth: 260}}
                />
            </Stack>

            <Stack spacing={2}>
                <ReportSummaryCard request={req}/>

                {q.data?.byAccount?.length ? (
                    <ReportSummaryByAccountTable rows={q.data.byAccount} currency={q.data.currency}/>
                ) : null}
            </Stack>
        </Box>
    );
}

import {
    Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography
} from "@mui/material";
import type {Moneyish, ReportSummaryByAccount} from "../reportsTypes";

function money(value: Moneyish, currency: string) {
    const n = typeof value === "number" ? value : Number(value);
    if (!Number.isFinite(n)) return `${value} ${currency}`;
    return new Intl.NumberFormat(undefined, {style: "currency", currency}).format(n);
}

export function ReportSummaryByAccountTable(props: {
    rows: ReportSummaryByAccount[];
    currency: string;
}) {
    return (
        <TableContainer component={Paper} variant="outlined">
            <Table size="small">
                <TableHead>
                    <TableRow>
                        <TableCell>Account</TableCell>
                        <TableCell align="right">Income</TableCell>
                        <TableCell align="right">Expense</TableCell>
                        <TableCell align="right">Net</TableCell>
                        <TableCell align="right">Count</TableCell>
                    </TableRow>
                </TableHead>

                <TableBody>
                    {props.rows.map((r) => (
                        <TableRow key={r.accountId} hover>
                            <TableCell>
                                <Typography variant="body2">{r.accountName}</Typography>
                            </TableCell>
                            <TableCell align="right">{money(r.income, props.currency)}</TableCell>
                            <TableCell align="right">{money(r.expense, props.currency)}</TableCell>
                            <TableCell align="right">{money(r.net, props.currency)}</TableCell>
                            <TableCell align="right">{r.count}</TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    );
}

import {
    IconButton,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import type {TransactionSummary} from "../api/types.ts";

type Props = {
    transactions: TransactionSummary[];
    onDelete: (id: number) => void;
    onOpen: (id: number) => void;
};

export default function TransactionsTable({transactions, onDelete, onOpen}: Props) {
    if (transactions.length === 0) return <div>No transactions</div>;

    return (
        <Table size="small">
            <TableHead>
                <TableRow>
                    <TableCell>Date</TableCell>
                    <TableCell>Category</TableCell>
                    <TableCell align="right">Amount</TableCell>
                    <TableCell/>
                </TableRow>
            </TableHead>

            <TableBody>
                {transactions.map((t) => (
                    <TableRow
                        key={t.id}
                        hover
                        sx={{cursor: "pointer"}}
                        onClick={() => onOpen(t.id)}
                    >
                        <TableCell>{new Date(t.occurredAt).toLocaleDateString()}</TableCell>
                        <TableCell>
                            {t.category.name}
                        </TableCell>
                        <TableCell align="right">{t.amount}</TableCell>
                        <TableCell align="right" onClick={(e) => e.stopPropagation()}>
                            <IconButton
                                size="small"
                                onClick={(e) => {
                                    e.stopPropagation();
                                    const ok = window.confirm("Delete this transaction?");
                                    if (ok) onDelete(t.id);
                                }}
                            >
                                <DeleteIcon fontSize="small"/>
                            </IconButton>
                        </TableCell>
                    </TableRow>
                ))}
            </TableBody>
        </Table>
    );
}

import {
    IconButton,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import type {TransactionSummary} from "../types/transaction";

type Props = {
    transactions: TransactionSummary[];
    onDelete: (id: number) => void;
};

export default function TransactionsTable({ transactions, onDelete }: Props) {
    if (transactions.length === 0) {
        return <div>No transactions</div>;
    }

    return (
        <Table size="small">
            <TableHead>
                <TableRow>
                    <TableCell>Date</TableCell>
                    <TableCell>Category</TableCell>
                    <TableCell>Description</TableCell>
                    <TableCell align="right">Amount</TableCell>
                    <TableCell />
                </TableRow>
            </TableHead>

            <TableBody>
                {transactions.map((t) => (
                    <TableRow key={t.id}>
                        <TableCell>
                            {new Date(t.occurredAt).toLocaleDateString()}
                        </TableCell>
                        <TableCell>{t.category.name}</TableCell>
                        <TableCell>-</TableCell>
                        <TableCell align="right">{t.amount}</TableCell>
                        <TableCell align="right">
                            <IconButton
                                size="small"
                                onClick={() => onDelete(t.id)}
                            >
                                <DeleteIcon fontSize="small" />
                            </IconButton>
                        </TableCell>
                    </TableRow>
                ))}
            </TableBody>
        </Table>
    );
}

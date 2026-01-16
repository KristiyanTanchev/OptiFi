import {
    Chip,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
} from "@mui/material";
import type {AccountSummary} from "../types/account";
import { useNavigate } from "react-router-dom";

type Props = {
    accounts: AccountSummary[];
};

export default function AccountsTable({ accounts }: Props) {
    const nav = useNavigate();

    if (accounts.length === 0) return <div>No accounts yet</div>;

    return (
        <Table>
            <TableHead>
                <TableRow>
                    <TableCell>Name</TableCell>
                    <TableCell>Type</TableCell>
                    <TableCell>Currency</TableCell>
                    <TableCell>Institution</TableCell>
                    <TableCell>Status</TableCell>
                </TableRow>
            </TableHead>

            <TableBody>
                {accounts.map((a) => (
                    <TableRow
                        key={a.id}
                        hover
                        sx={{ cursor: "pointer" }}
                        onClick={() => nav(`/accounts/${a.id}`)}
                    >
                        <TableCell>{a.name}</TableCell>
                        <TableCell>{a.type}</TableCell>
                        <TableCell>{a.currency}</TableCell>
                        <TableCell>{a.institution ?? "-"}</TableCell>
                        <TableCell>
                            {a.archived ? (
                                <Chip label="Archived" size="small" />
                            ) : (
                                <Chip label="Active" color="success" size="small" />
                            )}
                        </TableCell>
                    </TableRow>
                ))}
            </TableBody>
        </Table>
    );
}

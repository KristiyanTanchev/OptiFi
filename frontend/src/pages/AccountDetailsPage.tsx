import {
    Alert,
    Box,
    Button,
    Pagination,
    CircularProgress,
    Container,
    Stack,
    Typography,
} from "@mui/material";
import { useTransactions, useDeleteTransaction } from "../hooks/useTransactions";
import TransactionsTable from "../components/TransactionsTable";
import TransactionFiltersForm from "../components/TransactionFilters";
import CreateTransactionDialog from "../components/CreateTransactionDialog";
import { useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
    useAccount,
    useArchiveAccount,
    useDeleteAccount,
    useUnarchiveAccount,
} from "../hooks/useAccounts";
import EditAccountDialog from "../components/EditAccountDialog";
import EditTransactionDialog from "../components/EditTransactionDialog.tsx";

export default function AccountDetailsPage() {
    const { id } = useParams();
    const accountId = useMemo(() => Number(id), [id]);
    const nav = useNavigate();

    const { data, isLoading, error } = useAccount(accountId);
    const archive = useArchiveAccount(accountId);
    const unarchive = useUnarchiveAccount(accountId);
    const del = useDeleteAccount();

    const [editOpen, setEditOpen] = useState(false);

    const [page, setPage] = useState(0);
    const [filters, setFilters] = useState({});
    const [createOpen, setCreateOpen] = useState(false);
    const [editTxId, setEditTxId] = useState<number | null>(null);

    const tx = useTransactions(accountId, filters, page);
    const delTx = useDeleteTransaction(accountId);

    async function onDelete() {
        if (!data) return;
        const ok = window.confirm(`Delete account "${data.name}"? This cannot be undone.`);
        if (!ok) return;
        await del.mutateAsync(data.id);
        nav("/accounts", { replace: true });
    }

    return (
        <Container sx={{ py: 4 }}>
            <Stack spacing={3}>
                <Box display="flex" justifyContent="space-between" alignItems="center">
                    <Stack>
                        <Typography variant="h4">Account</Typography>
                        {data && (
                            <Typography color="text.secondary">
                                {data.name} • {data.type} • {data.currency}
                            </Typography>
                        )}
                    </Stack>

                    <Stack direction="row" spacing={1}>
                        <Button variant="outlined" onClick={() => nav("/accounts")}>
                            Back
                        </Button>

                        {data && (
                            <>
                                <Button variant="outlined" onClick={() => setEditOpen(true)}>
                                    Edit
                                </Button>

                                {data.archived ? (
                                    <Button
                                        variant="contained"
                                        onClick={() => unarchive.mutate()}
                                        disabled={unarchive.isPending}
                                    >
                                        Unarchive
                                    </Button>
                                ) : (
                                    <Button
                                        variant="contained"
                                        onClick={() => archive.mutate()}
                                        disabled={archive.isPending}
                                    >
                                        Archive
                                    </Button>
                                )}

                                <Button
                                    color="error"
                                    variant="outlined"
                                    onClick={onDelete}
                                    disabled={del.isPending}
                                >
                                    Delete
                                </Button>
                            </>
                        )}
                    </Stack>
                </Box>

                {isLoading && <CircularProgress />}
                {error && <Alert severity="error">Failed to load account</Alert>}

                {data && (
                    <Stack spacing={2} sx={{ mt: 3 }}>
                        <Stack direction="row" justifyContent="space-between">
                            <Typography variant="h6">Transactions</Typography>
                            <Button variant="contained" onClick={() => setCreateOpen(true)}>
                                New
                            </Button>
                        </Stack>

                        <TransactionFiltersForm value={filters} onChange={setFilters} />

                        {tx.data && (
                            <>
                                <TransactionsTable
                                    transactions={tx.data.content}
                                    onDelete={(id) => delTx.mutate(id)}
                                    onOpen={(id) => {
                                        setEditTxId(id);
                                        setEditOpen(true);
                                    }}
                                />

                                <EditTransactionDialog
                                    accountId={accountId}
                                    transactionId={editTxId}
                                    open={editOpen}
                                    onClose={() => setEditOpen(false)}
                                />

                                <Pagination
                                    page={tx.data.number + 1}
                                    count={tx.data.totalPages}
                                    onChange={(_, p) => setPage(p - 1)}
                                />
                            </>
                        )}

                        <CreateTransactionDialog
                            accountId={accountId}
                            open={createOpen}
                            onClose={() => setCreateOpen(false)}
                        />
                    </Stack>
                )}

                {data && (
                    <EditAccountDialog
                        open={editOpen}
                        onClose={() => setEditOpen(false)}
                        account={data}
                    />
                )}
            </Stack>
        </Container>
    );
}

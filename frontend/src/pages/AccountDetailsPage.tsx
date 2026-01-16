import {
    Alert,
    Box,
    Button,
    CircularProgress,
    Container,
    Stack,
    Typography,
} from "@mui/material";
import { useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
    useAccount,
    useArchiveAccount,
    useDeleteAccount,
    useUnarchiveAccount,
} from "../hooks/useAccounts";
import EditAccountDialog from "../components/EditAccountDialog";

export default function AccountDetailsPage() {
    const { id } = useParams();
    const accountId = useMemo(() => Number(id), [id]);
    const nav = useNavigate();

    const { data, isLoading, error } = useAccount(accountId);
    const archive = useArchiveAccount(accountId);
    const unarchive = useUnarchiveAccount(accountId);
    const del = useDeleteAccount();

    const [editOpen, setEditOpen] = useState(false);

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
                    <Stack spacing={1}>
                        <Typography><b>Institution:</b> {data.institution ?? "-"}</Typography>
                        <Typography><b>Status:</b> {data.archived ? "Archived" : "Active"}</Typography>
                        <Typography><b>Created:</b> {new Date(data.createdAt).toLocaleString()}</Typography>
                        {data.updatedAt && (
                            <Typography><b>Updated:</b> {new Date(data.updatedAt).toLocaleString()}</Typography>
                        )}

                        {/* We’ll do transactions UI next */}
                        <Box sx={{ mt: 2 }}>
                            <Typography variant="h6">Transactions</Typography>
                            <Typography color="text.secondary">
                                Next step: table + filters + pagination
                            </Typography>
                        </Box>
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

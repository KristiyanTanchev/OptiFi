import {
    Alert,
    Button,
    CircularProgress,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    MenuItem,
    Stack,
    TextField,
} from "@mui/material";
import { useEffect, useMemo, useState } from "react";
import { useCategories } from "../hooks/useCategories";
import { useTransactionDetails, useUpdateTransaction } from "../hooks/useTransactions";
import {useNotify} from "../ui/notify.tsx";

type Props = {
    accountId: number;
    transactionId: number | null;
    open: boolean;
    onClose: () => void;
};

function toDateInputValue(iso: string) {
    // yyyy-MM-dd for <input type="date">
    return new Date(iso).toISOString().slice(0, 10);
}

export default function EditTransactionDialog({ accountId, transactionId, open, onClose }: Props) {
    const details = useTransactionDetails(accountId, open ? transactionId : null);
    const { data: categories } = useCategories();

    const update = useMemo(() => {
        return transactionId != null ? useUpdateTransaction(accountId, transactionId) : null;
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [accountId, transactionId]);

    const [amount, setAmount] = useState("");
    const [date, setDate] = useState("");
    const [description, setDescription] = useState("");
    const [categoryId, setCategoryId] = useState("");

    const notify = useNotify();

    useEffect(() => {
        if (!open) return;
        if (!details.data) return;

        setAmount(details.data.amount);
        setDate(toDateInputValue(details.data.occurredAt));
        setDescription(details.data.description ?? "");
        setCategoryId(String(details.data.category.id));
    }, [open, details.data]);

    async function onSubmit() {
        if (!update || transactionId == null) return;

        await update.mutateAsync({
            amount,
            occurredAt: new Date(date).toISOString(),
            description: description || undefined,
            categoryId: Number(categoryId),
        });

        notify("Transaction updated", "success");

        onClose();
    }

    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
            <DialogTitle>Edit transaction</DialogTitle>
            <DialogContent>
                {details.isLoading && <CircularProgress />}
                {details.error && <Alert severity="error">Failed to load transaction</Alert>}

                {details.data && (
                    <Stack spacing={2} sx={{ mt: 2 }}>
                        <TextField
                            label="Amount"
                            value={amount}
                            onChange={(e) => setAmount(e.target.value)}
                            required
                        />

                        <TextField
                            label="Date"
                            type="date"
                            InputLabelProps={{ shrink: true }}
                            value={date}
                            onChange={(e) => setDate(e.target.value)}
                            required
                        />

                        <TextField
                            label="Description"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            fullWidth
                        />

                        <TextField
                            select
                            label="Category"
                            value={categoryId}
                            onChange={(e) => setCategoryId(e.target.value)}
                            required
                        >
                            {categories?.map((c) => (
                                <MenuItem key={c.id} value={c.id}>
                                    {c.icon} {c.name}
                                </MenuItem>
                            ))}
                        </TextField>
                    </Stack>
                )}
            </DialogContent>

            <DialogActions>
                <Button onClick={onClose} disabled={update?.isPending}>
                    Cancel
                </Button>
                <Button
                    variant="contained"
                    onClick={onSubmit}
                    disabled={!details.data || update?.isPending}
                >
                    Save
                </Button>
            </DialogActions>
        </Dialog>
    );
}

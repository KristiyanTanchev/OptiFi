import {
    Alert,
    Button,
    CircularProgress,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle, ListItemIcon, ListItemText,
    MenuItem,
    Stack,
    TextField,
} from "@mui/material";
import {useEffect, useState} from "react";
import {useTransactionDetails, useUpdateTransaction} from "../hooks/useTransactions.ts";
import {useCategories} from "@category/hooks/useCategories.ts";
import {useNotify} from "@shared/ui/notify.tsx";
import {getCategoryVisual} from "@category/components/categoryVisuals.tsx";

type Props = {
    accountId: number;
    transactionId: number | null;
    open: boolean;
    onClose: () => void;
};

function toDateInputValue(iso: string) {
    return new Date(iso).toISOString().slice(0, 10);
}

export default function EditTransactionDialog({accountId, transactionId, open, onClose}: Props) {
    const details = useTransactionDetails(accountId, open ? transactionId : null);
    const {data: categories} = useCategories();

    const update = useUpdateTransaction(accountId, transactionId, {
        enabled: open && transactionId != null,
    });


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
        if (transactionId == null) return;

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
                {details.isLoading && <CircularProgress/>}
                {details.error && <Alert severity="error">Failed to load transaction</Alert>}

                {details.data && (
                    <Stack spacing={2} sx={{mt: 2}}>
                        <TextField
                            label="Amount"
                            value={amount}
                            onChange={(e) => setAmount(e.target.value)}
                            required
                        />

                        <TextField
                            label="Date"
                            type="date"
                            value={date}
                            onChange={(e) => setDate(e.target.value)}
                            required
                            slotProps={{inputLabel: {shrink: true}}}
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
                            {categories?.map((c) => {
                                const v = getCategoryVisual(c.icon);

                                return (
                                    <MenuItem key={c.id} value={c.id}>
                                        <ListItemIcon sx={{minWidth: 32, color: v.color}}>
                                            {v.icon}
                                        </ListItemIcon>
                                        <ListItemText primary={c.name}/>
                                    </MenuItem>
                                );
                            })}
                        </TextField>
                    </Stack>
                )}
            </DialogContent>

            <DialogActions>
                <Button onClick={onClose} disabled={update.isPending}>
                    Cancel
                </Button>
                <Button
                    variant="contained"
                    onClick={onSubmit}
                    disabled={!details.data || update.isPending || transactionId == null}
                >
                    Save
                </Button>
            </DialogActions>
        </Dialog>
    );
}

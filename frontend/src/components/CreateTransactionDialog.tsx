import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Stack,
    TextField,
    MenuItem,
} from "@mui/material";
import { useState } from "react";
import { useCreateTransaction } from "../hooks/useTransactions";
import { useCategories } from "../hooks/useCategories";

type Props = {
    accountId: number;
    open: boolean;
    onClose: () => void;
};

export default function CreateTransactionDialog({ accountId, open, onClose }: Props) {
    const create = useCreateTransaction(accountId);

    const [amount, setAmount] = useState("");
    const [date, setDate] = useState("");
    const [categoryId, setCategoryId] = useState("");

    const { data: categories } = useCategories();

    async function onSubmit() {
        await create.mutateAsync({
            amount,
            occurredAt: new Date(date).toISOString(),
            categoryId: Number(categoryId),
        });
        onClose();
    }

    return (
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>New transaction</DialogTitle>
            <DialogContent>
                <Stack spacing={2} sx={{ mt: 1 }}>
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
            </DialogContent>

            <DialogActions>
                <Button onClick={onClose}>Cancel</Button>
                <Button variant="contained" onClick={onSubmit}>
                    Create
                </Button>
            </DialogActions>
        </Dialog>
    );
}

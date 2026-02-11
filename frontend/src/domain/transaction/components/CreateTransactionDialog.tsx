import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Stack,
    TextField,
    MenuItem,
    ListItemIcon,
    ListItemText,
} from "@mui/material";
import {useState} from "react";
import {useCreateTransaction} from "../hooks/useTransactions.ts";
import {useCategories} from "@category/hooks/useCategories.ts";
import {useNotify} from "@shared/ui/notify.tsx";
import {getCategoryVisual} from "@category/components/categoryVisuals.tsx";

type Props = {
    accountId: number;
    open: boolean;
    onClose: () => void;
};

export default function CreateTransactionDialog({accountId, open, onClose}: Props) {
    const create = useCreateTransaction(accountId);
    const notify = useNotify();

    const [amount, setAmount] = useState("");
    const [date, setDate] = useState("");
    const [categoryId, setCategoryId] = useState("");

    const {data: categories} = useCategories();

    async function onSubmit() {
        await create.mutateAsync({
            amount,
            occurredAt: new Date(date).toISOString(),
            categoryId: Number(categoryId),
        });
        notify("Transaction created", "success");
        onClose();
    }

    return (
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>New transaction</DialogTitle>
            <DialogContent>
                <Stack spacing={2} sx={{mt: 1}}>
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

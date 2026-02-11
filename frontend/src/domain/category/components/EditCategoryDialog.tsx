import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Stack,
    TextField,
} from "@mui/material";
import {useEffect, useState} from "react";
import type {CategorySummary} from "../api/types.ts";
import {useUpdateCategory} from "../hooks/useCategories.ts";

type Props = {
    open: boolean;
    onClose: () => void;
    category: CategorySummary;
};

export default function EditCategoryDialog({open, onClose, category}: Props) {
    const update = useUpdateCategory(category.id);

    const [name, setName] = useState(category.name);
    const [description, setDescription] = useState("");
    const [icon, setIcon] = useState(category.icon);

    // Since summary doesn’t include description, we require user to re-enter it
    // OR you can fetch details and prefill (better). If you want that, tell me and I’ll wire it.
    useEffect(() => {
        if (!open) return;
        setName(category.name);
        setIcon(category.icon);
        setDescription("");
    }, [open, category]);

    async function onSubmit() {
        await update.mutateAsync({name, description, icon});
        onClose();
    }

    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
            <DialogTitle>Edit category</DialogTitle>
            <DialogContent>
                <Stack spacing={2} sx={{mt: 1}}>
                    <TextField label="Name" value={name} onChange={(e) => setName(e.target.value)} required/>
                    <TextField
                        label="Description"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        required
                        helperText="Required by backend (3–255 chars)"
                    />
                    <TextField label="Icon" value={icon} onChange={(e) => setIcon(e.target.value)} required/>
                </Stack>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} disabled={update.isPending}>Cancel</Button>
                <Button variant="contained" onClick={onSubmit} disabled={update.isPending}>
                    Save
                </Button>
            </DialogActions>
        </Dialog>
    );
}

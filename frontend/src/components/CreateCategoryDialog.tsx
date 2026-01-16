import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Stack,
    TextField,
} from "@mui/material";
import { useState } from "react";
import { useCreateCategory } from "../hooks/useCategories";

type Props = {
    open: boolean;
    onClose: () => void;
};

export default function CreateCategoryDialog({ open, onClose }: Props) {
    const create = useCreateCategory();

    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [icon, setIcon] = useState("");

    async function onSubmit() {
        await create.mutateAsync({ name, description, icon });
        onClose();
        setName("");
        setDescription("");
        setIcon("");
    }

    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
            <DialogTitle>Create category</DialogTitle>
            <DialogContent>
                <Stack spacing={2} sx={{ mt: 1 }}>
                    <TextField
                        label="Name"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />
                    <TextField
                        label="Description"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        required
                    />
                    <TextField
                        label="Icon"
                        value={icon}
                        onChange={(e) => setIcon(e.target.value)}
                        required
                        helperText="Emoji or icon name"
                    />
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

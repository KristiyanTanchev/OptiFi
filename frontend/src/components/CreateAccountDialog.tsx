import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    MenuItem,
    Stack,
    TextField,
} from "@mui/material";
import { useState } from "react";
import { useCreateAccount } from "../hooks/useAccounts";
import {useNotify} from "../ui/notify.tsx";

type Props = {
    open: boolean;
    onClose: () => void;
};

const ACCOUNT_TYPES = ["CASH", "BANK"];
const CURRENCIES = ["EUR", "USD"];

export default function CreateAccountDialog({ open, onClose }: Props) {
    const createAccount = useCreateAccount();

    const [name, setName] = useState("");
    const [type, setType] = useState("");
    const [currency, setCurrency] = useState("");
    const [institution, setInstitution] = useState("");
    const notify = useNotify();

    function reset() {
        setName("");
        setType("");
        setCurrency("");
        setInstitution("");
    }

    function handleClose() {
        if (!createAccount.isPending) {
            reset();
            onClose();
        }
    }

    async function onSubmit() {
        await createAccount.mutateAsync({
            name,
            type,
            currency,
            institution: institution || undefined,
        });
        notify("Account created", "success");
        handleClose();
    }

    return (
        <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
            <DialogTitle>Create account</DialogTitle>
            <DialogContent>
                <Stack spacing={2} sx={{ mt: 1 }}>
                    <TextField
                        label="Name"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                        fullWidth
                    />

                    <TextField
                        select
                        label="Type"
                        value={type}
                        onChange={(e) => setType(e.target.value)}
                        required
                        fullWidth
                    >
                        {ACCOUNT_TYPES.map((t) => (
                            <MenuItem key={t} value={t}>
                                {t}
                            </MenuItem>
                        ))}
                    </TextField>

                    <TextField
                        select
                        label="Currency"
                        value={currency}
                        onChange={(e) => setCurrency(e.target.value)}
                        required
                        fullWidth
                    >
                        {CURRENCIES.map((c) => (
                            <MenuItem key={c} value={c}>
                                {c}
                            </MenuItem>
                        ))}
                    </TextField>

                    <TextField
                        label="Institution"
                        value={institution}
                        onChange={(e) => setInstitution(e.target.value)}
                        fullWidth
                    />
                </Stack>
            </DialogContent>

            <DialogActions>
                <Button onClick={handleClose}>Cancel</Button>
                <Button
                    variant="contained"
                    onClick={onSubmit}
                    disabled={createAccount.isPending}
                >
                    Create
                </Button>
            </DialogActions>
        </Dialog>
    );
}

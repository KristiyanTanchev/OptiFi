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
import { useEffect, useState } from "react";
import type { AccountDetails } from "../types/account";
import { useUpdateAccount } from "../hooks/useAccounts";

type Props = {
    open: boolean;
    onClose: () => void;
    account: AccountDetails;
};

const ACCOUNT_TYPES = ["CASH", "BANK", "CARD"];
const CURRENCIES = ["EUR", "USD"];

export default function EditAccountDialog({ open, onClose, account }: Props) {
    const update = useUpdateAccount(account.id);

    const [name, setName] = useState(account.name);
    const [type, setType] = useState(account.type);
    const [currency, setCurrency] = useState(account.currency);
    const [institution, setInstitution] = useState(account.institution ?? "");

    useEffect(() => {
        if (!open) return;
        setName(account.name);
        setType(account.type);
        setCurrency(account.currency);
        setInstitution(account.institution ?? "");
    }, [open, account]);

    async function onSubmit() {
        await update.mutateAsync({
            name,
            type,
            currency,
            institution: institution || undefined,
        });
        onClose();
    }

    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
            <DialogTitle>Edit account</DialogTitle>
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
                <Button onClick={onClose} disabled={update.isPending}>
                    Cancel
                </Button>
                <Button variant="contained" onClick={onSubmit} disabled={update.isPending}>
                    Save
                </Button>
            </DialogActions>
        </Dialog>
    );
}

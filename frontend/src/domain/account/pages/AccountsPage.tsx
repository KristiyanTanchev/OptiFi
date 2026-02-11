import {
    Box,
    Button,
    CircularProgress,
    Container,
    Stack,
    Typography,
} from "@mui/material";
import {useState} from "react";
import {useAccounts} from "../hooks/useAccounts.ts";
import CreateAccountDialog from "../components/CreateAccountDialog.tsx";
import AccountsTable from "../components/AccountsTable.tsx";

export default function AccountsPage() {
    const {data, isLoading, error} = useAccounts();
    const [open, setOpen] = useState(false);

    return (
        <Container sx={{py: 4}}>
            <Stack spacing={3}>
                <Box display="flex" justifyContent="space-between" alignItems="center">
                    <Typography variant="h4">Accounts</Typography>
                    <Button variant="contained" onClick={() => setOpen(true)}>
                        New account
                    </Button>
                </Box>

                {isLoading && <CircularProgress/>}
                {error && <Typography color="error">Failed to load accounts</Typography>}

                {data && <AccountsTable accounts={data}/>}

                <CreateAccountDialog open={open} onClose={() => setOpen(false)}/>
            </Stack>
        </Container>
    );
}

import { Button, Container, Stack, Typography } from "@mui/material";
import { clearSession, getUser } from "../auth/session";
import { useNavigate } from "react-router-dom";

export default function DashboardPage() {
    const nav = useNavigate();
    const user = getUser();

    return (
        <Container sx={{ py: 4 }}>
            <Stack spacing={2}>
                <Typography variant="h4">Dashboard</Typography>
                <Typography>Logged in as: {user?.username} ({user?.role})</Typography>

                <Button
                    variant="outlined"
                    onClick={() => {
                        clearSession();
                        nav("/login", { replace: true });
                    }}
                >
                    Logout
                </Button>
            </Stack>
        </Container>
    );
}

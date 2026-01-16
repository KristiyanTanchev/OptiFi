import { useState } from "react";
import {useNavigate} from "react-router-dom";
import { Alert, Box, Button, Container, Paper, Stack, TextField, Typography } from "@mui/material";
import { login } from "../auth/authApi";
import { saveSession } from "../auth/session";

export default function LoginPage() {
    const nav = useNavigate();

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    async function onSubmit(e: React.FormEvent) {
        e.preventDefault();
        setError(null);
        setLoading(true);
        try {
            const res = await login({ username, password });
            saveSession(res);
            nav("/", { replace: true });
        } catch (err: any) {
            setError(err?.response?.data?.message ?? "Login failed");
        } finally {
            setLoading(false);
        }
    }

    return (
        <Container maxWidth="sm">
            <Box sx={{ minHeight: "100vh", display: "grid", placeItems: "center" }}>
                <Paper elevation={3} sx={{ p: 4, width: "100%" }}>
                    <Stack spacing={2}>
                        <Typography variant="h4">OptiFI</Typography>
                        <Typography variant="body2" color="text.secondary">
                            Sign in to continue
                        </Typography>

                        {error && <Alert severity="error">{error}</Alert>}

                        <Box component="form" onSubmit={onSubmit}>
                            <Stack spacing={2}>
                                <TextField
                                    label="Username"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    autoComplete="username"
                                    required
                                    fullWidth
                                />
                                <TextField
                                    label="Password"
                                    type="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    autoComplete="current-password"
                                    required
                                    fullWidth
                                />
                                <Button type="submit" variant="contained" disabled={loading}>
                                    {loading ? "Signing in..." : "Sign in"}
                                </Button>
                                <Button variant="outlined" onClick={() => nav("/register")}>
                                    Register
                                </Button>
                            </Stack>
                        </Box>
                    </Stack>
                </Paper>
            </Box>
        </Container>
    );
}

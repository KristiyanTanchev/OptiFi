import {
    AppBar,
    Box,
    Button,
    Drawer,
    List,
    ListItemButton,
    ListItemText,
    Toolbar,
    Typography,
} from "@mui/material";
import { Outlet, useNavigate } from "react-router-dom";
import { clearSession, getUser } from "../auth/session";

const drawerWidth = 220;

export default function AppLayout() {
    const nav = useNavigate();
    const user = getUser();

    function logout() {
        clearSession();
        nav("/login", { replace: true });
    }

    return (
        <Box sx={{ display: "flex" }}>
            {/* Top bar */}
            <AppBar
                position="fixed"
                sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}
            >
                <Toolbar sx={{ display: "flex", justifyContent: "space-between" }}>
                    <Typography variant="h6">OptiFI</Typography>

                    <Box display="flex" alignItems="center" gap={2}>
                        <Typography variant="body2">
                            {user?.username} ({user?.role})
                        </Typography>
                        <Button color="inherit" onClick={logout}>
                            Logout
                        </Button>
                    </Box>
                </Toolbar>
            </AppBar>

            {/* Sidebar */}
            <Drawer
                variant="permanent"
                sx={{
                    width: drawerWidth,
                    flexShrink: 0,
                    [`& .MuiDrawer-paper`]: { width: drawerWidth, boxSizing: "border-box" },
                }}
            >
                <Toolbar />
                <List>
                    <ListItemButton onClick={() => nav("/accounts")}>
                        <ListItemText primary="Accounts" />
                    </ListItemButton>
                </List>
            </Drawer>

            {/* Page content */}
            <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
                <Toolbar />
                <Outlet />
            </Box>
        </Box>
    );
}

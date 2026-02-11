import {Alert, Snackbar} from "@mui/material";
import React, {createContext, useCallback, useContext, useMemo, useState} from "react";

type Severity = "success" | "info" | "warning" | "error";

type NotifyFn = (message: string, severity?: Severity) => void;

const NotifyContext = createContext<NotifyFn | null>(null);

export function NotifyProvider({children}: { children: React.ReactNode }) {
    const [open, setOpen] = useState(false);
    const [message, setMessage] = useState("");
    const [severity, setSeverity] = useState<Severity>("info");

    const notify = useCallback<NotifyFn>((msg, sev = "info") => {
        setMessage(msg);
        setSeverity(sev);
        setOpen(true);
    }, []);

    const value = useMemo(() => notify, [notify]);

    return (
        <NotifyContext.Provider value={value}>
            {children}
            <Snackbar
                open={open}
                autoHideDuration={3500}
                onClose={() => setOpen(false)}
                anchorOrigin={{vertical: "bottom", horizontal: "center"}}
            >
                <Alert onClose={() => setOpen(false)} severity={severity} variant="filled">
                    {message}
                </Alert>
            </Snackbar>
        </NotifyContext.Provider>
    );
}

export function useNotify() {
    const ctx = useContext(NotifyContext);
    if (!ctx) throw new Error("useNotify must be used inside NotifyProvider");
    return ctx;
}

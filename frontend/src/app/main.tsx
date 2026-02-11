import React from "react";
import ReactDOM from "react-dom/client";
import {BrowserRouter} from "react-router-dom";
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import {CssBaseline} from "@mui/material";
import {NotifyProvider} from "@shared/ui/notify.tsx";
import {GoogleOAuthProvider} from "@react-oauth/google";
import App from "./App.tsx";

const queryClient = new QueryClient();

ReactDOM.createRoot(document.getElementById("root")!).render(
    <React.StrictMode>
        <QueryClientProvider client={queryClient}>
            <NotifyProvider>
                <BrowserRouter>
                    <GoogleOAuthProvider clientId={import.meta.env.VITE_GOOGLE_CLIENT_ID}>
                        <CssBaseline/>
                        <App/>
                    </GoogleOAuthProvider>
                </BrowserRouter>
            </NotifyProvider>
        </QueryClientProvider>
    </React.StrictMode>
);

import axios from "axios";
import {clearSession} from "../auth/session.ts";

const baseURL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export const http = axios.create({baseURL});

http.interceptors.request.use((config) => {
    const token = localStorage.getItem("optifi_token");
    if (token) {
        config.headers = config.headers ?? {};
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

http.interceptors.response.use(
    (res) => res,
    (error) => {
        const status = error?.response?.status;
        if (status === 401) {
            // token expired/invalid
            clearSession();
            // force redirect (works even outside React)
            window.location.href = "/login";
        }
        return Promise.reject(error);
    }
);

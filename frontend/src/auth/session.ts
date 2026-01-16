import type { LoginResponse } from "./types";

const TOKEN_KEY = "optifi_token";
const USER_KEY = "optifi_user";

export function saveSession(res: LoginResponse) {
    localStorage.setItem(TOKEN_KEY, res.token);
    localStorage.setItem(USER_KEY, JSON.stringify({ id: res.id, username: res.username, role: res.role }));
}

export function clearSession() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
}

export function getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
}

export function getUser(): { id: number; username: string; role: string } | null {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
}

export function isLoggedIn(): boolean {
    return !!getToken();
}

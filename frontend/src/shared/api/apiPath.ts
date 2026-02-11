import {http} from "./http.ts";

/**
 * If VITE_API_BASE_URL ends with `/api`, do NOT prefix paths with `/api`.
 * If it does not, DO prefix with `/api`.
 */
export function apiPath(path: string) {
    const base = (http.defaults.baseURL ?? "").toString().replace(/\/+$/, "");
    const p = path.startsWith("/") ? path : `/${path}`;

    // baseURL already includes /api
    if (base.endsWith("/api")) return p;

    // otherwise add /api prefix
    return p.startsWith("/api/") || p === "/api" ? p : `/api${p}`;
}

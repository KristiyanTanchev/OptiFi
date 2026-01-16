import { http } from "../api/http";
import type { LoginRequest, LoginResponse, RegisterRequest } from "./types";

export async function login(req: LoginRequest): Promise<LoginResponse> {
    const { data } = await http.post<LoginResponse>("/api/auth/login", req);
    return data;
}

export async function register(req: RegisterRequest): Promise<LoginResponse> {
    const { data } = await http.post<LoginResponse>("/api/auth/register", req);
    return data;
}

import {http} from "@shared/api/http.ts";
import type {LoginRequest, LoginResponse, RegisterRequest} from "./types.ts";

export async function login(req: LoginRequest): Promise<LoginResponse> {
    const {data} = await http.post<LoginResponse>("/api/auth/login", req);
    return data;
}

export async function register(req: RegisterRequest): Promise<LoginResponse> {
    const {data} = await http.post<LoginResponse>("/api/auth/register", req);
    return data;
}

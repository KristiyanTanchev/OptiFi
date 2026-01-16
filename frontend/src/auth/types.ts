export type Role = "ADMIN" | "USER" | "MODERATOR" | "BLOCKED" | "WAITING_APPROVAL";

export type LoginResponse = {
    token: string;
    type: "Bearer";
    id: number;
    username: string;
    role: Role;
};

export type LoginRequest = {
    username: string;
    password: string;
};

export type RegisterRequest = {
    username: string;
    password: string;
    confirmPassword: string;
    email: string;
}
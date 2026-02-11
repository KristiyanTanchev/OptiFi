import {http} from "@shared/api/http.ts";
import type {
    CategorySummary,
    CategoryDetails,
    CategoryCreateRequest,
    CategoryUpdateRequest,
} from "./types.ts";

export const categoriesApi = {
    getAll(): Promise<CategorySummary[]> {
        return http.get("/api/categories").then(r => r.data);
    },

    getById(id: number): Promise<CategoryDetails> {
        return http.get(`/api/categories/${id}`).then(r => r.data);
    },

    create(data: CategoryCreateRequest): Promise<CategoryDetails> {
        return http.post("/api/categories", data).then(r => r.data);
    },

    update(id: number, data: CategoryUpdateRequest): Promise<void> {
        return http.put(`/api/categories/${id}`, data).then(() => {
        });
    },

    delete(id: number): Promise<void> {
        return http.delete(`/api/categories/${id}`).then(() => {
        });
    },
};

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { categoriesApi } from "../api/categoriesApi";
import type {CategoryCreateRequest, CategoryUpdateRequest} from "../types/category";

export function useCategories() {
    return useQuery({
        queryKey: ["categories"],
        queryFn: categoriesApi.getAll,
    });
}

export function useCreateCategory() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (data: CategoryCreateRequest) =>
            categoriesApi.create(data),
        onSuccess: () =>
            qc.invalidateQueries({ queryKey: ["categories"] }),
    });
}

export function useUpdateCategory(id: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (data: CategoryUpdateRequest) =>
            categoriesApi.update(id, data),
        onSuccess: () =>
            qc.invalidateQueries({ queryKey: ["categories"] }),
    });
}

export function useDeleteCategory() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) =>
            categoriesApi.delete(id),
        onSuccess: () =>
            qc.invalidateQueries({ queryKey: ["categories"] }),
    });
}

import { Box, IconButton, Stack, Table, TableBody, TableCell, TableHead, TableRow } from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import { useState } from "react";
import type { CategorySummary } from "../types/category";
import { useDeleteCategory } from "../hooks/useCategories";
import EditCategoryDialog from "./EditCategoryDialog";
import { getCategoryVisual } from "../categories/categoryVisuals"; // <-- adjust path to wherever you put it

type Props = { categories: CategorySummary[] };

export default function CategoriesTable({ categories }: Props) {
    const del = useDeleteCategory();
    const [edit, setEdit] = useState<CategorySummary | null>(null);

    if (categories.length === 0) return <div>No categories</div>;

    return (
        <>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>Icon</TableCell>
                        <TableCell>Name</TableCell>
                        <TableCell />
                    </TableRow>
                </TableHead>

                <TableBody>
                    {categories.map((c) => {
                        const v = getCategoryVisual(c.icon);

                        return (
                            <TableRow key={c.id}>
                                <TableCell>
                                    <Box sx={{ display: "flex", alignItems: "center", color: v.color }}>
                                        {v.icon}
                                    </Box>
                                </TableCell>

                                <TableCell>{c.name}</TableCell>

                                <TableCell align="right">
                                    <Stack direction="row" spacing={1} justifyContent="flex-end">
                                        {c.canEdit && (
                                            <IconButton size="small" onClick={() => setEdit(c)}>
                                                <EditIcon fontSize="small" />
                                            </IconButton>
                                        )}

                                        {c.canDelete && (
                                            <IconButton
                                                size="small"
                                                onClick={() => {
                                                    const ok = window.confirm(`Delete category "${c.name}"?`);
                                                    if (ok) del.mutate(c.id);
                                                }}
                                            >
                                                <DeleteIcon fontSize="small" />
                                            </IconButton>
                                        )}
                                    </Stack>
                                </TableCell>
                            </TableRow>
                        );
                    })}
                </TableBody>
            </Table>

            {edit && (
                <EditCategoryDialog
                    open={!!edit}
                    category={edit}
                    onClose={() => setEdit(null)}
                />
            )}
        </>
    );
}

import {
    IconButton,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import type {CategorySummary} from "../types/category";
import { useDeleteCategory } from "../hooks/useCategories";

type Props = {
    categories: CategorySummary[];
};

export default function CategoriesTable({ categories }: Props) {
    const del = useDeleteCategory();

    if (categories.length === 0) return <div>No categories</div>;

    return (
        <Table>
            <TableHead>
                <TableRow>
                    <TableCell>Icon</TableCell>
                    <TableCell>Name</TableCell>
                    <TableCell />
                </TableRow>
            </TableHead>

            <TableBody>
                {categories.map((c) => (
                    <TableRow key={c.id}>
                        <TableCell>{c.icon}</TableCell>
                        <TableCell>{c.name}</TableCell>
                        <TableCell align="right">
                            <IconButton
                                size="small"
                                onClick={() => {
                                    const ok = window.confirm(`Delete category "${c.name}"?`);
                                    if (ok) del.mutate(c.id);
                                }}
                            >
                                <DeleteIcon fontSize="small" />
                            </IconButton>
                        </TableCell>
                    </TableRow>
                ))}
            </TableBody>
        </Table>
    );
}

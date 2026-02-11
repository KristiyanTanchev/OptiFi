import {
    Box,
    Button,
    CircularProgress,
    Container,
    Stack,
    Typography,
} from "@mui/material";
import {useEffect, useState} from "react";
import {useCategories} from "../hooks/useCategories.ts";
import CategoriesTable from "../components/CategoriesTable.tsx";
import CreateCategoryDialog from "../components/CreateCategoryDialog.tsx";
import {useFeatures} from "@meta/hooks/useFeatures.ts";


export default function CategoriesPage() {
    const {data, isLoading, error} = useCategories();
    const [open, setOpen] = useState(false);

    const [, setCreateCategoryEnabled] = useState(false);
    const {data: features} = useFeatures();
    const canCreateCategories = !!features?.createCategoryEnabled;


    useEffect(() => {
        (async () => {
            try {
                const res = await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/public/features`);
                if (!res.ok) return;
                console.log("features status:", res.status, "body:", res.text());
                const flags = await res.json();
                console.log("features:", flags);
                setCreateCategoryEnabled(Boolean(flags?.createCategoryEnabled));
            } catch {
                setCreateCategoryEnabled(false);
            }
        })();
    }, []);

    return (
        <Container sx={{py: 4}}>
            <Stack spacing={3}>
                {canCreateCategories ? (
                    <Box display="flex" justifyContent="space-between">
                        <Typography variant="h4">Categories</Typography>
                        <Button variant="contained" onClick={() => setOpen(true)}>
                            New category
                        </Button>
                    </Box>
                ) : ""}


                {isLoading && <CircularProgress/>}
                {error && <Typography color="error">Failed to load categories</Typography>}

                {data && <CategoriesTable categories={data}/>}

                <CreateCategoryDialog open={open} onClose={() => setOpen(false)}/>
            </Stack>
        </Container>
    );
}

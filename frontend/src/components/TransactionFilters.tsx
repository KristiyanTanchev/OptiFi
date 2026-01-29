import { Stack, TextField } from "@mui/material";
import type { TransactionFilters } from "../types/transaction";

type Props = {
    value: TransactionFilters;
    onChange: (v: TransactionFilters) => void;
};

// Converts "YYYY-MM-DD" -> "YYYY-MM-DDT00:00:00Z"
function dateOnlyToInstant(dateOnly: string): string {
    return `${dateOnly}T00:00:00Z`;
}

// Converts "YYYY-MM-DDTHH:mm:ssZ" -> "YYYY-MM-DD" for the date input
function instantToDateOnly(instant: string): string {
    // Works for ISO strings like 2026-01-20T00:00:00Z
    return instant.slice(0, 10);
}

export default function TransactionFiltersForm({ value, onChange }: Props) {
    const startDateUi = value.startDate ? instantToDateOnly(value.startDate) : "";
    const endDateUi = value.endDate ? instantToDateOnly(value.endDate) : "";

    return (
        <Stack direction="row" spacing={2}>
            <TextField
                label="From"
                type="date"
                InputLabelProps={{ shrink: true }}
                value={startDateUi}
                onChange={(e) => {
                    const d = e.target.value;
                    onChange({
                        ...value,
                        startDate: d ? dateOnlyToInstant(d) : undefined,
                    });
                }}
            />
            <TextField
                label="To"
                type="date"
                InputLabelProps={{ shrink: true }}
                value={endDateUi}
                onChange={(e) => {
                    const d = e.target.value;
                    onChange({
                        ...value,
                        endDate: d ? dateOnlyToInstant(d) : undefined,
                    });
                }}
            />
            <TextField
                label="Min"
                value={value.min ?? ""}
                onChange={(e) =>
                    onChange({ ...value, min: e.target.value || undefined })
                }
            />
            <TextField
                label="Max"
                value={value.max ?? ""}
                onChange={(e) =>
                    onChange({ ...value, max: e.target.value || undefined })
                }
            />
        </Stack>
    );
}

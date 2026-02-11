import {Stack, TextField} from "@mui/material";
import type {TransactionFilters} from "../api/types.ts";

type Props = {
    value: TransactionFilters;
    onChange: (v: TransactionFilters) => void;
};

export default function TransactionFiltersForm({value, onChange}: Props) {
    const startDateUi = value.startDate;
    const endDateUi = value.endDate;

    return (
        <Stack direction="row" spacing={2}>
            <TextField
                label="From"
                type="date"
                value={startDateUi}
                onChange={(e) => {
                    const d = e.target.value;
                    onChange({
                        ...value,
                        startDate: d,
                    });
                }}
                slotProps={{
                    inputLabel: {shrink: true},
                }}
            />

            <TextField
                label="To"
                type="date"
                value={endDateUi}
                onChange={(e) => {
                    const d = e.target.value;
                    onChange({
                        ...value,
                        endDate: d,
                    });
                }}
                slotProps={{
                    inputLabel: {shrink: true},
                }}
            />
            <TextField
                label="Min"
                value={value.min ?? ""}
                onChange={(e) =>
                    onChange({...value, min: e.target.value || undefined})
                }
            />
            <TextField
                label="Max"
                value={value.max ?? ""}
                onChange={(e) =>
                    onChange({...value, max: e.target.value || undefined})
                }
            />
        </Stack>
    );
}

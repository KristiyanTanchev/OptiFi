import { Stack, TextField } from "@mui/material";
import type {TransactionFilters} from "../types/transaction";

type Props = {
    value: TransactionFilters;
    onChange: (v: TransactionFilters) => void;
};

export default function TransactionFiltersForm({ value, onChange }: Props) {
    return (
        <Stack direction="row" spacing={2}>
            <TextField
                label="From"
                type="date"
                InputLabelProps={{ shrink: true }}
                value={value.startDate ?? ""}
                onChange={(e) =>
                    onChange({ ...value, startDate: e.target.value || undefined })
                }
            />
            <TextField
                label="To"
                type="date"
                InputLabelProps={{ shrink: true }}
                value={value.endDate ?? ""}
                onChange={(e) =>
                    onChange({ ...value, endDate: e.target.value || undefined })
                }
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

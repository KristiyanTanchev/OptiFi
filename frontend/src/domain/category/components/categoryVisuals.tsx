import type {ReactElement} from "react";

import RestaurantIcon from "@mui/icons-material/Restaurant";
import ShoppingCartIcon from "@mui/icons-material/ShoppingCart";
import HomeIcon from "@mui/icons-material/Home";
import DirectionsBusIcon from "@mui/icons-material/DirectionsBus";
import DirectionsCarIcon from "@mui/icons-material/DirectionsCar";
import MovieIcon from "@mui/icons-material/Movie";
import ComputerIcon from "@mui/icons-material/Computer";
import ReceiptLongIcon from "@mui/icons-material/ReceiptLong";
import TrendingUpIcon from "@mui/icons-material/TrendingUp";
import AttachMoneyIcon from "@mui/icons-material/AttachMoney";
import CategoryIcon from "@mui/icons-material/Category";

export type CategoryVisual = {
    icon: ReactElement;
    color: string; // hex
};

// Keys must match CategorySummary.icon coming from backend
export const CATEGORY_VISUALS: Record<string, CategoryVisual> = {
    restaurant: {icon: <RestaurantIcon fontSize="small"/>, color: "#F97316"},
    shopping_cart: {icon: <ShoppingCartIcon fontSize="small"/>, color: "#EC4899"},
    home: {icon: <HomeIcon fontSize="small"/>, color: "#8B5CF6"},
    directions_bus: {icon: <DirectionsBusIcon fontSize="small"/>, color: "#3B82F6"},
    directions_car: {icon: <DirectionsCarIcon fontSize="small"/>, color: "#64748B"},
    movie: {icon: <MovieIcon fontSize="small"/>, color: "#A855F7"},
    computer: {icon: <ComputerIcon fontSize="small"/>, color: "#06B6D4"},
    receipt_long: {icon: <ReceiptLongIcon fontSize="small"/>, color: "#EF4444"},
    trending_up: {icon: <TrendingUpIcon fontSize="small"/>, color: "#22C55E"},
    attach_money: {icon: <AttachMoneyIcon fontSize="small"/>, color: "#14B8A6"},
};

const FALLBACK: CategoryVisual = {
    icon: <CategoryIcon fontSize="small"/>,
    color: "#64748B",
};

export function getCategoryVisual(iconKey?: string | null): CategoryVisual {
    if (!iconKey) return FALLBACK;
    return CATEGORY_VISUALS[iconKey] ?? FALLBACK;
}

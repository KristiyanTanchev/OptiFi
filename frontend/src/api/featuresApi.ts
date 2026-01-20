import { http } from "./http";
import type { FeatureFlags } from "../types/features";

export async function getFeatureFlags(): Promise<FeatureFlags> {
    const res = await http.get<FeatureFlags>("/api/public/features");
    return res.data;
}

import {http} from "@shared/api/http.ts";
import type {FeatureFlags} from "./types.ts";

export async function getFeatureFlags(): Promise<FeatureFlags> {
    const res = await http.get<FeatureFlags>("/api/public/features");
    return res.data;
}

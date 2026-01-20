import { useQuery } from "@tanstack/react-query";
import { getFeatureFlags } from "../api/featuresApi";

export function useFeatures() {
    return useQuery({
        queryKey: ["features"],
        queryFn: getFeatureFlags
    });
}

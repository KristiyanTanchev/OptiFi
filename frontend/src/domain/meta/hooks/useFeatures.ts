import {useQuery} from "@tanstack/react-query";
import {getFeatureFlags} from "../api/featuresApi.ts";

export function useFeatures() {
    return useQuery({
        queryKey: ["features"],
        queryFn: getFeatureFlags
    });
}

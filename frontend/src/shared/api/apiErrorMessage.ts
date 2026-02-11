export function apiErrorMessage(err: any, fallback = "Something went wrong") {
    return err?.response?.data?.message
        ?? err?.response?.data?.error
        ?? err?.message
        ?? fallback;
}

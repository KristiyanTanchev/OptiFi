import { GoogleLogin } from "@react-oauth/google";
import { useNavigate } from "react-router-dom";
import { http } from "../api/http";
import { saveSession } from "../auth/session";

export function GoogleLoginButton() {
    const nav = useNavigate();

    return (
        <GoogleLogin
            onSuccess={async (cred) => {
                console.log("google cred", cred);

                const idToken = cred.credential;
                console.log("idToken present?", !!idToken);

                const res = await http.post("/api/auth/oidc/google", { idToken });
                console.log("exchange response", res.data);

                saveSession(res.data);
                console.log("stored token", localStorage.getItem("optifi_token"));

                nav("/", { replace: true });
            }}
            onError={() => {
                alert("Google login failed");
            }}
        />
    );
}

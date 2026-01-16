import {Routes, Route, Navigate} from "react-router-dom";
import RequireAuth from "./auth/RequireAuth";
import LoginPage from "./pages/LoginPage";
import AccountsPage from "./pages/AccountsPage";
import AppLayout from "./layout/AppLayout";
import RegisterPage from "./pages/RegisterPage";
import AccountDetailsPage from "./pages/AccountDetailsPage";
import CategoriesPage from "./pages/CategoriesPage";

export default function App() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage/>}/>
            <Route path="/register" element={<RegisterPage/>}/>

            <Route element={<RequireAuth/>}>
                <Route element={<AppLayout/>}>
                    <Route path="/accounts" element={<AccountsPage />} />
                    <Route path="/accounts/:id" element={<AccountDetailsPage />} />
                    <Route path="/" element={<Navigate to="/accounts" replace />} />
                    <Route path="/categories" element={<CategoriesPage />} />
                </Route>
            </Route>

            <Route path="*" element={<Navigate to="/" replace/>}/>
        </Routes>
    );
}

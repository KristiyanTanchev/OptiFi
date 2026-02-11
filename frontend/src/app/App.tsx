import {Routes, Route, Navigate} from "react-router-dom";
import RequireAuth from "@shared/auth/RequireAuth.tsx";
import LoginPage from "@auth/pages/LoginPage.tsx";
import AccountsPage from "@account/pages/AccountsPage.tsx";
import AppLayout from "@layout/AppLayout.tsx";
import RegisterPage from "@auth/pages/RegisterPage.tsx";
import AccountDetailsPage from "@account/pages/AccountDetailsPage.tsx";
import CategoriesPage from "@category/pages/CategoriesPage.tsx";
import ReportsPage from "@reporting/pages/ReportsPage.tsx";

export default function App() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage/>}/>
            <Route path="/register" element={<RegisterPage/>}/>

            <Route element={<RequireAuth/>}>
                <Route element={<AppLayout/>}>
                    <Route path="/accounts" element={<AccountsPage/>}/>
                    <Route path="/accounts/:id" element={<AccountDetailsPage/>}/>
                    <Route path="/" element={<Navigate to="/accounts" replace/>}/>
                    <Route path="/categories" element={<CategoriesPage/>}/>
                    <Route path="/reports" element={<ReportsPage/>}/>
                </Route>
            </Route>

            <Route path="*" element={<Navigate to="/" replace/>}/>
        </Routes>
    );
}

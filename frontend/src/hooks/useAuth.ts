import { useState } from "react";
import { login } from "../services/api";

export function useAuth() {
    const [token, setToken] = useState(
        localStorage.getItem("token") ?? ""
    );

    const [email, setEmail] = useState(
        "abhishek@example.com"
    );

    const [password, setPassword] = useState(
        "password123"
    );

    const [message, setMessage] = useState("");
    const [loading, setLoading] = useState(false);

    async function handleLogin() {
        setLoading(true);
        setMessage("");

        try {
            const data = await login(email, password);

            localStorage.setItem("token", data.token);
            setToken(data.token);

            return true;
        } catch {
            setMessage("Invalid email or password.");
            return false;
        } finally {
            setLoading(false);
        }
    }

    function handleLogout() {
        localStorage.removeItem("token");
        setToken("");
    }

    return {
        token,
        email,
        password,
        message,
        loading,

        setEmail,
        setPassword,
        setMessage,

        handleLogin,
        handleLogout,
    };
}
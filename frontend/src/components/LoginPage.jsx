import { useState } from "react";
import { login, register } from "../services/DataService";

function LoginPage({ onLogin }) {
    const [mode, setMode] = useState("login"); // "login" or "register"
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");
        setLoading(true);
        try {
            if (mode === "register") {
                await register(username, password);
            }
            const data = await login(username, password);
            onLogin(data.username); // tell App.jsx (child -> parent)
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    function switchMode() {
        setMode(mode === "login" ? "register" : "login");
        setError("");
    }

    return (
        <div className="card login-card">
            <h2>{mode === "login" ? "Hotel Booking – Login" : "Create an account"}</h2>

            {error && <div className="error">{error}</div>}

            <form onSubmit={handleSubmit}>
                <label>
                    Username
                    <input value={username} onChange={(e) => setUsername(e.target.value)} required />
                </label>
                <label>
                    Password (min 6 characters)
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </label>
                <button type="submit" disabled={loading}>
                    {loading ? "Please wait..." : mode === "login" ? "Login" : "Register"}
                </button>
            </form>

            <p className="muted">
                {mode === "login" ? "No account? " : "Already registered? "}
                <button type="button" className="link-button" onClick={switchMode}>
                    {mode === "login" ? "Register" : "Login"}
                </button>
            </p>
        </div>
    );
}

export default LoginPage;
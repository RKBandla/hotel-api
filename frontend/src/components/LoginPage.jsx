import { useState } from "react";
import { login, register } from "../services/DataService";

const EMPTY_FORM = { username: "", password: "", firstName: "", lastName: "", email: "" };

function LoginPage({ onLogin }) {
    const [mode, setMode] = useState("login"); // "login" or "register"
    const [form, setForm] = useState(EMPTY_FORM);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    function update(field, value) {
        setForm({ ...form, [field]: value });
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");
        setLoading(true);
        try {
            if (mode === "register") {
                await register(form); // always creates a GUEST
            }
            await login(form.username, form.password);
            onLogin(); // tell App.jsx (child -> parent)
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
            <h2>{mode === "login" ? "Hotel Booking – Login" : "Create a guest account"}</h2>

            {error && <div className="error">{error}</div>}

            <form onSubmit={handleSubmit}>
                <label>
                    Username
                    <input value={form.username} onChange={(e) => update("username", e.target.value)} required />
                </label>
                <label>
                    Password (min 6 characters)
                    <input
                        type="password"
                        value={form.password}
                        onChange={(e) => update("password", e.target.value)}
                        required
                    />
                </label>

                {mode === "register" && (
                    <>
                        <label>
                            First name
                            <input value={form.firstName} onChange={(e) => update("firstName", e.target.value)} required />
                        </label>
                        <label>
                            Last name
                            <input value={form.lastName} onChange={(e) => update("lastName", e.target.value)} required />
                        </label>
                        <label>
                            Email
                            <input
                                type="email"
                                value={form.email}
                                onChange={(e) => update("email", e.target.value)}
                                required
                            />
                        </label>
                    </>
                )}

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
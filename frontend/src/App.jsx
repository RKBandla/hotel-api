import { useState } from "react";
import { getCurrentUser, logout } from "./services/DataService";
import LoginPage from "./components/LoginPage";
import Header from "./components/Header";
import RoomsPage from "./components/RoomsPage";
import GuestsPage from "./components/GuestsPage";
import BookingsPage from "./components/BookingsPage";

function App() {
    const [user, setUser] = useState(getCurrentUser()); // { username, role, guestId } or null
    const [page, setPage] = useState("rooms");

    function handleLogin() {
        setUser(getCurrentUser());
        setPage("rooms");
    }

    function handleLogout() {
        logout();
        setUser(null);
    }

    if (!user) {
        return <LoginPage onLogin={handleLogin} />;
    }

    const isAdmin = user.role === "ADMIN";

    return (
        <div>
            <Header user={user} page={page} onNavigate={setPage} onLogout={handleLogout} />
            <main className="container">
                {page === "rooms" && <RoomsPage isAdmin={isAdmin} />}
                {page === "guests" && isAdmin && <GuestsPage />}
                {page === "bookings" && <BookingsPage isAdmin={isAdmin} />}
            </main>
        </div>
    );
}

export default App;
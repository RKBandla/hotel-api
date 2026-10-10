import { useState } from "react";
import { getUsername, logout } from "./services/DataService";
import LoginPage from "./components/LoginPage";
import Header from "./components/Header";
import RoomsPage from "./components/RoomsPage";
import GuestsPage from "./components/GuestsPage";
import BookingsPage from "./components/BookingsPage";

function App() {
    const [username, setUsername] = useState(getUsername());
    const [page, setPage] = useState("rooms");

    function handleLogout() {
        logout();
        setUsername(null);
    }

    if (!username) {
        return <LoginPage onLogin={setUsername} />;
    }

    return (
        <div>
            <Header username={username} page={page} onNavigate={setPage} onLogout={handleLogout} />
            <main className="container">
                {page === "rooms" && <RoomsPage />}
                {page === "guests" && <GuestsPage />}
                {page === "bookings" && <BookingsPage />}
            </main>
        </div>
    );
}

export default App;
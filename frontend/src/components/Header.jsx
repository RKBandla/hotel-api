function Header({ user, page, onNavigate, onLogout }) {
    const isAdmin = user.role === "ADMIN";

    // Guests tab only for ADMIN; a guest sees "My bookings"
    const pages = [
        { key: "rooms", label: "Rooms" },
        ...(isAdmin ? [{ key: "guests", label: "Guests" }] : []),
        { key: "bookings", label: isAdmin ? "Bookings" : "My bookings" },
    ];

    return (
        <header className="header">
            <h1>🏨 Hotel Booking</h1>
            <nav>
                {pages.map((p) => (
                    <button
                        key={p.key}
                        className={page === p.key ? "active" : ""}
                        onClick={() => onNavigate(p.key)}
                    >
                        {p.label}
                    </button>
                ))}
            </nav>
            <div>
        <span>
          Hi, {user.username} <span className="role-badge">{user.role}</span>{" "}
        </span>
                <button className="secondary" onClick={onLogout}>
                    Logout
                </button>
            </div>
        </header>
    );
}

export default Header;
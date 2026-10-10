function Header({ username, page, onNavigate, onLogout }) {
    const pages = [
        { key: "rooms", label: "Rooms" },
        { key: "guests", label: "Guests" },
        { key: "bookings", label: "Bookings" },
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
                <span>Hi, {username} </span>
                <button className="secondary" onClick={onLogout}>
                    Logout
                </button>
            </div>
        </header>
    );
}

export default Header;
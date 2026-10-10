import { useEffect, useState } from "react";
import { getRooms, createRoom, deleteRoom, getAvailableRooms } from "../services/DataService";

const EMPTY_ROOM = { number: "", type: "SINGLE", pricePerNight: "", capacity: 1 };

// isAdmin = true  -> can add and delete rooms
// isAdmin = false -> guest: can only look at rooms and search availability
function RoomsPage({ isAdmin }) {
    const [rooms, setRooms] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");
    const [form, setForm] = useState(EMPTY_ROOM);
    const [search, setSearch] = useState({ checkIn: "", checkOut: "", type: "" });
    const [showingAvailable, setShowingAvailable] = useState(false);

    async function loadRooms() {
        setLoading(true);
        setError("");
        try {
            const data = await getRooms();
            setRooms(data || []);
            setShowingAvailable(false);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadRooms();
    }, []);

    async function handleCreate(e) {
        e.preventDefault();
        setError("");
        setMessage("");
        try {
            await createRoom({
                number: form.number,
                type: form.type,
                pricePerNight: Number(form.pricePerNight),
                capacity: Number(form.capacity),
            });
            setMessage("Room " + form.number + " created");
            setForm(EMPTY_ROOM);
            loadRooms();
        } catch (err) {
            setError(err.message);
        }
    }

    async function handleDelete(room) {
        if (!window.confirm("Delete room " + room.number + "?")) return;
        setError("");
        setMessage("");
        try {
            await deleteRoom(room.id);
            setMessage("Room " + room.number + " deleted");
            loadRooms();
        } catch (err) {
            setError(err.message);
        }
    }

    async function handleSearch(e) {
        e.preventDefault();
        setError("");
        setMessage("");
        try {
            const data = await getAvailableRooms(search.checkIn, search.checkOut, search.type);
            setRooms(data || []);
            setShowingAvailable(true);
        } catch (err) {
            setError(err.message);
        }
    }

    return (
        <div>
            <h2>Rooms</h2>

            {error && <div className="error">{error}</div>}
            {message && <div className="success">{message}</div>}

            {isAdmin && (
                <div className="card">
                    <h3>Add a room</h3>
                    <form onSubmit={handleCreate} className="form-row">
                        <label>
                            Number
                            <input
                                value={form.number}
                                onChange={(e) => setForm({ ...form, number: e.target.value })}
                                required
                            />
                        </label>
                        <label>
                            Type
                            <select value={form.type} onChange={(e) => setForm({ ...form, type: e.target.value })}>
                                <option value="SINGLE">SINGLE</option>
                                <option value="DOUBLE">DOUBLE</option>
                                <option value="SUITE">SUITE</option>
                            </select>
                        </label>
                        <label>
                            Price per night
                            <input
                                type="number"
                                step="0.01"
                                value={form.pricePerNight}
                                onChange={(e) => setForm({ ...form, pricePerNight: e.target.value })}
                                required
                            />
                        </label>
                        <label>
                            Capacity
                            <input
                                type="number"
                                min="1"
                                value={form.capacity}
                                onChange={(e) => setForm({ ...form, capacity: e.target.value })}
                                required
                            />
                        </label>
                        <label>
                            <button type="submit">Add room</button>
                        </label>
                    </form>
                </div>
            )}

            <div className="card">
                <h3>Find available rooms</h3>
                <form onSubmit={handleSearch} className="form-row">
                    <label>
                        Check-in
                        <input
                            type="date"
                            value={search.checkIn}
                            onChange={(e) => setSearch({ ...search, checkIn: e.target.value })}
                            required
                        />
                    </label>
                    <label>
                        Check-out
                        <input
                            type="date"
                            value={search.checkOut}
                            onChange={(e) => setSearch({ ...search, checkOut: e.target.value })}
                            required
                        />
                    </label>
                    <label>
                        Type
                        <select value={search.type} onChange={(e) => setSearch({ ...search, type: e.target.value })}>
                            <option value="">Any</option>
                            <option value="SINGLE">SINGLE</option>
                            <option value="DOUBLE">DOUBLE</option>
                            <option value="SUITE">SUITE</option>
                        </select>
                    </label>
                    <label>
                        <button type="submit">Search</button>
                    </label>
                    {showingAvailable && (
                        <label>
                            <button type="button" className="secondary" onClick={loadRooms}>
                                Show all rooms
                            </button>
                        </label>
                    )}
                </form>
            </div>

            <div className="card">
                <h3>{showingAvailable ? "Available rooms" : "All rooms"}</h3>
                {loading ? (
                    <p>Loading...</p>
                ) : rooms.length === 0 ? (
                    <p className="muted">No rooms found.</p>
                ) : (
                    <div className="table-wrap">
                        <table>
                            <thead>
                            <tr>
                                <th>Number</th>
                                <th>Type</th>
                                <th>Price / night</th>
                                <th>Capacity</th>
                                {isAdmin && <th></th>}
                            </tr>
                            </thead>
                            <tbody>
                            {rooms.map((room) => (
                                <tr key={room.id}>
                                    <td>{room.number}</td>
                                    <td>{room.type}</td>
                                    <td>${Number(room.pricePerNight).toFixed(2)}</td>
                                    <td>{room.capacity}</td>
                                    {isAdmin && (
                                        <td>
                                            <button className="danger" onClick={() => handleDelete(room)}>
                                                Delete
                                            </button>
                                        </td>
                                    )}
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    );
}

export default RoomsPage;
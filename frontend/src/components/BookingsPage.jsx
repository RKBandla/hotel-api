import { useEffect, useState } from "react";
import {
    getBookings,
    getRooms,
    getGuests,
    createBooking,
    cancelBooking,
} from "../services/DataService";

const EMPTY_BOOKING = { roomId: "", guestId: "", checkIn: "", checkOut: "" };

// isAdmin = true  -> all bookings, can book for any guest
// isAdmin = false -> guest: only their own bookings, always books for themselves
function BookingsPage({ isAdmin }) {
    const [bookings, setBookings] = useState([]);
    const [rooms, setRooms] = useState([]);
    const [guests, setGuests] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");
    const [form, setForm] = useState(EMPTY_BOOKING);

    async function loadAll() {
        setLoading(true);
        setError("");
        try {
            const [bookingData, roomData, guestData] = await Promise.all([
                getBookings(),
                getRooms(),
                isAdmin ? getGuests() : Promise.resolve([]), // a guest is not allowed to list guests (403)
            ]);
            setBookings(bookingData || []);
            setRooms(roomData || []);
            setGuests(guestData || []);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadAll();
    }, []);

    function roomNumber(roomId) {
        const room = rooms.find((r) => r.id === roomId);
        return room ? room.number : roomId;
    }

    function guestName(guestId) {
        const guest = guests.find((g) => g.id === guestId);
        return guest ? guest.firstName + " " + guest.lastName : guestId;
    }

    async function handleCreate(e) {
        e.preventDefault();
        setError("");
        setMessage("");
        try {
            const body = { roomId: form.roomId, checkIn: form.checkIn, checkOut: form.checkOut };
            if (isAdmin) {
                body.guestId = form.guestId; // only the admin chooses a guest
            }
            const booking = await createBooking(body);
            setMessage(
                "Booked room " + roomNumber(booking.roomId) +
                " – total $" + Number(booking.totalPrice).toFixed(2)
            );
            setForm(EMPTY_BOOKING);
            loadAll();
        } catch (err) {
            setError(err.message); // e.g. "Room 101 is already booked for those dates"
        }
    }

    async function handleCancel(booking) {
        if (!window.confirm("Cancel this booking?")) return;
        setError("");
        setMessage("");
        try {
            await cancelBooking(booking.id);
            setMessage("Booking cancelled");
            loadAll();
        } catch (err) {
            setError(err.message);
        }
    }

    const canBook = rooms.length > 0 && (!isAdmin || guests.length > 0);

    return (
        <div>
            <h2>{isAdmin ? "All bookings" : "My bookings"}</h2>

            {error && <div className="error">{error}</div>}
            {message && <div className="success">{message}</div>}

            <div className="card">
                <h3>New booking</h3>
                {!canBook ? (
                    <p className="muted">
                        {isAdmin ? "Add at least one room and one guest first." : "No rooms available yet."}
                    </p>
                ) : (
                    <form onSubmit={handleCreate} className="form-row">
                        <label>
                            Room
                            <select
                                value={form.roomId}
                                onChange={(e) => setForm({ ...form, roomId: e.target.value })}
                                required
                            >
                                <option value="">-- choose --</option>
                                {rooms.map((r) => (
                                    <option key={r.id} value={r.id}>
                                        {r.number} ({r.type}, ${Number(r.pricePerNight).toFixed(2)})
                                    </option>
                                ))}
                            </select>
                        </label>
                        {isAdmin && (
                            <label>
                                Guest
                                <select
                                    value={form.guestId}
                                    onChange={(e) => setForm({ ...form, guestId: e.target.value })}
                                    required
                                >
                                    <option value="">-- choose --</option>
                                    {guests.map((g) => (
                                        <option key={g.id} value={g.id}>
                                            {g.firstName} {g.lastName}
                                        </option>
                                    ))}
                                </select>
                            </label>
                        )}
                        <label>
                            Check-in
                            <input
                                type="date"
                                value={form.checkIn}
                                onChange={(e) => setForm({ ...form, checkIn: e.target.value })}
                                required
                            />
                        </label>
                        <label>
                            Check-out
                            <input
                                type="date"
                                value={form.checkOut}
                                onChange={(e) => setForm({ ...form, checkOut: e.target.value })}
                                required
                            />
                        </label>
                        <label>
                            <button type="submit">Book</button>
                        </label>
                    </form>
                )}
            </div>

            <div className="card">
                <h3>{isAdmin ? "All bookings" : "Your bookings"}</h3>
                {loading ? (
                    <p>Loading...</p>
                ) : bookings.length === 0 ? (
                    <p className="muted">No bookings yet.</p>
                ) : (
                    <div className="table-wrap">
                        <table>
                            <thead>
                            <tr>
                                <th>Room</th>
                                {isAdmin && <th>Guest</th>}
                                <th>Check-in</th>
                                <th>Check-out</th>
                                <th>Total</th>
                                <th>Status</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            {bookings.map((b) => (
                                <tr key={b.id}>
                                    <td>{roomNumber(b.roomId)}</td>
                                    {isAdmin && <td>{guestName(b.guestId)}</td>}
                                    <td>{b.checkIn}</td>
                                    <td>{b.checkOut}</td>
                                    <td>${Number(b.totalPrice).toFixed(2)}</td>
                                    <td><span className={"badge " + b.status}>{b.status}</span></td>
                                    <td>
                                        {b.status === "ACTIVE" && (
                                            <button className="danger" onClick={() => handleCancel(b)}>
                                                Cancel
                                            </button>
                                        )}
                                    </td>
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

export default BookingsPage;
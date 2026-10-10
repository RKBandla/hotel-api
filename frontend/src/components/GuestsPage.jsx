import { useEffect, useState } from "react";
import { getGuests, createGuest, deleteGuest, getGuestBookings } from "../services/DataService";

const EMPTY_GUEST = { firstName: "", lastName: "", email: "" };

function GuestsPage() {
    const [guests, setGuests] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");
    const [form, setForm] = useState(EMPTY_GUEST);
    const [selectedGuest, setSelectedGuest] = useState(null);
    const [guestBookings, setGuestBookings] = useState([]);

    async function loadGuests() {
        setLoading(true);
        setError("");
        try {
            const data = await getGuests();
            setGuests(data || []);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadGuests();
    }, []);

    async function handleCreate(e) {
        e.preventDefault();
        setError("");
        setMessage("");
        try {
            await createGuest(form);
            setMessage("Guest " + form.firstName + " created");
            setForm(EMPTY_GUEST);
            loadGuests();
        } catch (err) {
            setError(err.message);
        }
    }

    async function handleDelete(guest) {
        if (!window.confirm("Delete guest " + guest.firstName + "?")) return;
        setError("");
        setMessage("");
        try {
            await deleteGuest(guest.id);
            setMessage("Guest " + guest.firstName + " deleted");
            if (selectedGuest && selectedGuest.id === guest.id) {
                setSelectedGuest(null);
            }
            loadGuests();
        } catch (err) {
            setError(err.message);
        }
    }

    async function handleViewBookings(guest) {
        setError("");
        try {
            const data = await getGuestBookings(guest.id);
            setGuestBookings(data || []);
            setSelectedGuest(guest);
        } catch (err) {
            setError(err.message);
        }
    }

    return (
        <div>
            <h2>Guests</h2>

            {error && <div className="error">{error}</div>}
            {message && <div className="success">{message}</div>}

            <div className="card">
                <h3>Add a guest</h3>
                <form onSubmit={handleCreate} className="form-row">
                    <label>
                        First name
                        <input
                            value={form.firstName}
                            onChange={(e) => setForm({ ...form, firstName: e.target.value })}
                            required
                        />
                    </label>
                    <label>
                        Last name
                        <input
                            value={form.lastName}
                            onChange={(e) => setForm({ ...form, lastName: e.target.value })}
                            required
                        />
                    </label>
                    <label>
                        Email
                        <input
                            type="email"
                            value={form.email}
                            onChange={(e) => setForm({ ...form, email: e.target.value })}
                            required
                        />
                    </label>
                    <label>
                        <button type="submit">Add guest</button>
                    </label>
                </form>
            </div>

            <div className="card">
                <h3>All guests</h3>
                {loading ? (
                    <p>Loading...</p>
                ) : guests.length === 0 ? (
                    <p className="muted">No guests yet.</p>
                ) : (
                    <div className="table-wrap">
                        <table>
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th>Email</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            {guests.map((guest) => (
                                <tr key={guest.id}>
                                    <td>{guest.firstName} {guest.lastName}</td>
                                    <td>{guest.email}</td>
                                    <td>
                                        <button className="secondary" onClick={() => handleViewBookings(guest)}>
                                            Bookings
                                        </button>{" "}
                                        <button className="danger" onClick={() => handleDelete(guest)}>
                                            Delete
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>

            {selectedGuest && (
                <div className="card">
                    <h3>Bookings for {selectedGuest.firstName} {selectedGuest.lastName}</h3>
                    {guestBookings.length === 0 ? (
                        <p className="muted">This guest has no bookings.</p>
                    ) : (
                        <div className="table-wrap">
                            <table>
                                <thead>
                                <tr>
                                    <th>Check-in</th>
                                    <th>Check-out</th>
                                    <th>Total</th>
                                    <th>Status</th>
                                </tr>
                                </thead>
                                <tbody>
                                {guestBookings.map((b) => (
                                    <tr key={b.id}>
                                        <td>{b.checkIn}</td>
                                        <td>{b.checkOut}</td>
                                        <td>${Number(b.totalPrice).toFixed(2)}</td>
                                        <td><span className={"badge " + b.status}>{b.status}</span></td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}

export default GuestsPage;
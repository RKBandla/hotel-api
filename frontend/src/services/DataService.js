const TOKEN_KEY = "hotelToken";
const USER_KEY = "hotelUser";

export function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

export function getUsername() {
    return localStorage.getItem(USER_KEY);
}

export function logout() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
}

async function request(path, options = {}) {
    const headers = { "Content-Type": "application/json" };
    const token = getToken();
    if (token) {
        headers["Authorization"] = "Bearer " + token;
    }

    const response = await fetch("/api" + path, { ...options, headers });

    if (response.status === 401 && !path.startsWith("/auth")) {
        logout();
        window.location.reload();
        return null;
    }

    if (response.status === 204) {
        return null;
    }

    const text = await response.text();
    let data = null;
    if (text) {
        try {
            data = JSON.parse(text);
        } catch (e) {
            data = { error: text };
        }
    }

    if (!response.ok) {
        let message = (data && data.error) || "Request failed (" + response.status + ")";
        if (data && data.fields) {
            const details = Object.entries(data.fields)
                .map(([field, msg]) => field + " " + msg)
                .join(", ");
            message += ": " + details;
        }
        throw new Error(message);
    }

    return data;
}

export function register(username, password) {
    return request("/auth/register", {
        method: "POST",
        body: JSON.stringify({ username, password }),
    });
}

export async function login(username, password) {
    const data = await request("/auth/login", {
        method: "POST",
        body: JSON.stringify({ username, password }),
    });
    localStorage.setItem(TOKEN_KEY, data.token);
    localStorage.setItem(USER_KEY, data.username);
    return data;
}

export function getRooms() {
    return request("/rooms");
}

export function getAvailableRooms(checkIn, checkOut, type) {
    let path = "/rooms/available?checkIn=" + checkIn + "&checkOut=" + checkOut;
    if (type) {
        path += "&type=" + type;
    }
    return request(path);
}

export function createRoom(room) {
    return request("/rooms", { method: "POST", body: JSON.stringify(room) });
}

export function deleteRoom(id) {
    return request("/rooms/" + id, { method: "DELETE" });
}

export function getGuests() {
    return request("/guests");
}

export function createGuest(guest) {
    return request("/guests", { method: "POST", body: JSON.stringify(guest) });
}

export function deleteGuest(id) {
    return request("/guests/" + id, { method: "DELETE" });
}

export function getGuestBookings(id) {
    return request("/guests/" + id + "/bookings");
}

export function getBookings() {
    return request("/bookings");
}

export function createBooking(booking) {
    return request("/bookings", { method: "POST", body: JSON.stringify(booking) });
}

export function cancelBooking(id) {
    return request("/bookings/" + id + "/cancel", { method: "POST" });
}
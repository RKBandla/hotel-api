package com.example.hotel_api.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.example.hotel_api.models.AppUser;
import com.example.hotel_api.models.Role;

class JwtUtilTest {

    private static final String SECRET = "test-secret-for-unit-tests-0123456789abcdef";

    private final JwtUtil jwtUtil = new JwtUtil(SECRET, 3600000);   // 1 hour

    private final AppUser guest = new AppUser("rohan", "hash", Role.GUEST, "g1");
    private final AppUser admin = new AppUser("admin", "hash", Role.ADMIN, null);

    @Test
    void guestTokenCarriesUsernameRoleAndGuestId() {
        AuthUser user = jwtUtil.parse(jwtUtil.generateToken(guest));

        assertEquals("rohan", user.username());
        assertEquals("GUEST", user.role());
        assertEquals("g1", user.guestId());
        assertFalse(user.isAdmin());
    }

    @Test
    void adminTokenHasAdminRoleAndNoGuestId() {
        AuthUser user = jwtUtil.parse(jwtUtil.generateToken(admin));

        assertTrue(user.isAdmin());
        assertNull(user.guestId());
    }

    @Test
    void tamperedTokenIsRejected() {
        // take the GUEST token and swap in the ADMIN payload -> signature no longer matches
        String[] guestParts = jwtUtil.generateToken(guest).split("\\.");
        String[] adminParts = jwtUtil.generateToken(admin).split("\\.");
        String forged = guestParts[0] + "." + adminParts[1] + "." + guestParts[2];

        assertNull(jwtUtil.parse(forged));
    }

    @Test
    void tokenFromAnotherSecretIsRejected() {
        JwtUtil other = new JwtUtil("a-completely-different-secret-0123456789", 3600000);

        assertNull(jwtUtil.parse(other.generateToken(guest)));
    }

    @Test
    void expiredTokenIsRejected() {
        JwtUtil alreadyExpired = new JwtUtil(SECRET, -1000);   // expired 1 second ago

        assertNull(jwtUtil.parse(alreadyExpired.generateToken(guest)));
    }

    @Test
    void garbageIsRejected() {
        assertNull(jwtUtil.parse("not-a-token"));
        assertNull(jwtUtil.extractUsername("not-a-token"));
    }
}
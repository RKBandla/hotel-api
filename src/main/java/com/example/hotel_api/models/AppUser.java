package com.example.hotel_api.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class AppUser {

    @Id
    private String id;

    private String username;

    private String password;   // BCrypt hash, never plain text

    private Role role;         // ADMIN or GUEST

    private String guestId;    // which Guest profile this login belongs to (null for admin)

    public AppUser() {
    }

    public AppUser(String username, String password, Role role, String guestId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.guestId = guestId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getGuestId() { return guestId; }
    public void setGuestId(String guestId) { this.guestId = guestId; }
}
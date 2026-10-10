package com.example.hotel_api.repos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.example.hotel_api.models.Room;

// In-memory storage (Step 1). Step 2 replaces this with MongoDB.
@Repository
public class RoomRepository {

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public List<Room> findAll() {
        return new ArrayList<>(rooms.values());
    }

    public Optional<Room> findById(String id) {
        return Optional.ofNullable(rooms.get(id));
    }

    public boolean existsById(String id) {
        return rooms.containsKey(id);
    }

    public Room save(Room item) {
        if (item.getId() == null) {
            item.setId(String.valueOf(idCounter.incrementAndGet()));
        }
        rooms.put(item.getId(), item);
        return item;
    }

    public void deleteById(String id) {
        rooms.remove(id);
    }
}
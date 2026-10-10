package com.example.hotel_api.repos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.example.hotel_api.models.Guest;

@Repository
public class GuestRepository {

    private final Map<String, Guest> guests = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public List<Guest> findAll() {
        return new ArrayList<>(guests.values());
    }

    public Optional<Guest> findById(String id) {
        return Optional.ofNullable(guests.get(id));
    }

    public boolean existsById(String id) {
        return guests.containsKey(id);
    }

    public Guest save(Guest item) {
        if (item.getId() == null) {
            item.setId(String.valueOf(idCounter.incrementAndGet()));
        }
        guests.put(item.getId(), item);
        return item;
    }

    public void deleteById(String id) {
        guests.remove(id);
    }
}
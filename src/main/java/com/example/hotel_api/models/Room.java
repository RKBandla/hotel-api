package com.example.hotel_api.models;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Document(collection = "rooms")   // stored in the "rooms" collection
public class Room {

    @Id                               // MongoDB _id
    private String id;

    @NotBlank(message = "number is required")
    private String number;              // e.g. "101"

    @NotNull(message = "type is required (SINGLE, DOUBLE or SUITE)")
    private RoomType type;

    @NotNull(message = "pricePerNight is required")
    @DecimalMin(value = "1.00", message = "pricePerNight must be at least 1.00")
    @Field(targetType = FieldType.DECIMAL128)   // store money as an exact decimal
    private BigDecimal pricePerNight;

    @Min(value = 1, message = "capacity must be at least 1")
    private int capacity;

    public Room() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public RoomType getType() { return type; }
    public void setType(RoomType type) { this.type = type; }

    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}
package com.ticketsystem.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents an event in the ticket reservation system.
 */
public class Event {
    private String eventId;
    private String name;
    private LocalDateTime date;
    private String location;
    private EventType type;
    private List<TicketCategory> categories;

    public Event(String name, LocalDateTime date, String location, EventType type) {
        this.eventId = UUID.randomUUID().toString();
        this.name = name;
        this.date = date;
        this.location = location;
        this.type = type;
        this.categories = new ArrayList<>();
    }

    public void addTicketCategory(TicketCategory category) {
        categories.add(category);
    }

    public boolean removeTicketCategory(String categoryId) {
        return categories.removeIf(category -> category.getCategoryId().equals(categoryId));
    }

    public List<TicketCategory> getAvailableTickets() {
        return categories.stream()
                .filter(category -> category.getAvailableTickets() > 0)
                .toList();
    }

    // Getters and Setters
    public String getEventId() {
        return eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public EventType getType() {
        return type;
    }

    public List<TicketCategory> getCategories() {
        return new ArrayList<>(categories);
    }
} 
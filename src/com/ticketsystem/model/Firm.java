package com.ticketsystem.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a firm that can create and manage events.
 */
public class Firm {
    private String firmId;
    private String name;
    private List<Event> events;

    public Firm(String name) {
        this.firmId = UUID.randomUUID().toString();
        this.name = name;
        this.events = new ArrayList<>();
    }

    public Event createEvent(Event event) {
        events.add(event);
        return event;
    }

    public boolean removeEvent(String eventId) {
        return events.removeIf(event -> event.getEventId().equals(eventId));
    }

    public List<Event> getEvents() {
        return new ArrayList<>(events);
    }

    // Getters and Setters
    public String getFirmId() {
        return firmId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
} 
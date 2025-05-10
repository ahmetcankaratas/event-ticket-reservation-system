package com.ticketsystem.service;

import com.ticketsystem.model.Event;
import com.ticketsystem.model.TicketCategory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class to handle event-related operations.
 */
public class EventService {
    private List<Event> events;

    public EventService() {
        this.events = new ArrayList<>();
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public List<Event> searchEventsByTimeInterval(LocalDateTime start, LocalDateTime end) {
        return events.stream()
                .filter(event -> !event.getDate().isBefore(start) && !event.getDate().isAfter(end))
                .collect(Collectors.toList());
    }

    public Event getEventById(String eventId) {
        return events.stream()
                .filter(event -> event.getEventId().equals(eventId))
                .findFirst()
                .orElse(null);
    }

    public List<TicketCategory> getAvailableTicketsForEvent(String eventId) {
        Event event = getEventById(eventId);
        if (event == null) {
            return new ArrayList<>();
        }
        return event.getAvailableTickets();
    }

    public boolean removeEvent(String eventId) {
        return events.removeIf(event -> event.getEventId().equals(eventId));
    }

    public List<Event> getAllEvents() {
        return new ArrayList<>(events);
    }
} 
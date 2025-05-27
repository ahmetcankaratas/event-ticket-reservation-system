package com.ticketsystem.service;

import com.ticketsystem.model.Event;
import com.ticketsystem.model.TicketCategory;
import com.ticketsystem.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service class to handle event-related operations.
 */
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void addEvent(Event event) {
        eventRepository.save(event);
    }

    public List<Event> searchEventsByTimeInterval(LocalDateTime start, LocalDateTime end) {
        return eventRepository.findEventsByTimeInterval(start, end);
    }

    public Event getEventById(UUID eventId) {
        return eventRepository.findById(eventId).orElse(null);
    }

    public List<Event> getEventsByOrganizer(UUID organizerId) {
        return eventRepository.findEventsByOrganizer(organizerId);
    }

    public List<TicketCategory> getAvailableTicketsForEvent(UUID eventId) {
        Event event = getEventById(eventId);
        if (event == null) {
            return new ArrayList<>();
        }
        return event.getAvailableTickets();
    }

    public void removeEvent(UUID eventId) {
        eventRepository.deleteById(eventId);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
} 
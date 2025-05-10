package com.ticketsystem.service;

import com.ticketsystem.model.Event;
import com.ticketsystem.model.Reservation;
import com.ticketsystem.model.TicketCategory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class to handle reservation-related operations.
 */
public class ReservationService {
    private List<Reservation> reservations;
    private EventService eventService;

    public ReservationService(EventService eventService) {
        this.reservations = new ArrayList<>();
        this.eventService = eventService;
    }

    public Reservation createReservation(String eventId, String categoryId, int quantity, String userId) {
        Event event = eventService.getEventById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }

        Optional<TicketCategory> category = event.getCategories().stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst();

        if (category.isEmpty()) {
            throw new IllegalArgumentException("Ticket category not found");
        }

        if (!category.get().reserveTickets(quantity)) {
            throw new IllegalArgumentException("Not enough tickets available");
        }

        Reservation reservation = new Reservation(event, category.get(), quantity, userId);
        reservations.add(reservation);
        return reservation;
    }

    public Reservation getReservationById(String reservationId) {
        return reservations.stream()
                .filter(reservation -> reservation.getReservationId().equals(reservationId))
                .findFirst()
                .orElse(null);
    }

    public List<Reservation> getReservationsByUserId(String userId) {
        return reservations.stream()
                .filter(reservation -> userId.equals(reservation.getUserId()))
                .toList();
    }

    public boolean cancelReservation(String reservationId) {
        Reservation reservation = getReservationById(reservationId);
        if (reservation == null) {
            return false;
        }
        return reservation.cancelReservation();
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }
} 
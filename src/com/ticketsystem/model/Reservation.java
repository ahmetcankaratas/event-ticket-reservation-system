package com.ticketsystem.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a ticket reservation in the system.
 */
public class Reservation {
    private UUID reservationId;
    private Event event;
    private TicketCategory category;
    private int quantity;
    private LocalDateTime reservationDate;
    private String status;
    private UUID userId; // Can be null for anonymous reservations

    public Reservation(Event event, TicketCategory category, int quantity, UUID userId) {
        this.reservationId = UUID.randomUUID();
        this.event = event;
        this.category = category;
        this.quantity = quantity;
        this.reservationDate = LocalDateTime.now();
        this.status = "CONFIRMED";
        this.userId = userId;
    }

    public Reservation(UUID reservationId, Event event, TicketCategory category, int quantity, LocalDateTime reservationDate, String status, UUID userId) {
        this.reservationId = reservationId;
        this.event = event;
        this.category = category;
        this.quantity = quantity;
        this.reservationDate = reservationDate;
        this.status = status;
        this.userId = userId;
    }

    public String generateReservationNumber() {
        // Format: First 8 characters of reservationId
        return reservationId.toString().substring(0, 8).toUpperCase();
    }

    public boolean cancelReservation() {
        if ("CANCELLED".equals(status)) {
            return false;
        }
        status = "CANCELLED";
        category.updateAvailability(category.getAvailableTickets() + quantity);
        return true;
    }

    // Getters
    public UUID getReservationId() {
        return reservationId;
    }

    public Event getEvent() {
        return event;
    }

    public TicketCategory getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public String getStatus() {
        return status;
    }

    public UUID getUserId() {
        return userId;
    }

    public double getTotalPrice() {
        return category.getPrice() * quantity;
    }
} 
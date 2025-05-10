package com.ticketsystem.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a ticket reservation in the system.
 */
public class Reservation {
    private String reservationId;
    private Event event;
    private TicketCategory category;
    private int quantity;
    private LocalDateTime reservationDate;
    private String status;
    private String userId; // Can be null for anonymous reservations

    public Reservation(Event event, TicketCategory category, int quantity, String userId) {
        this.reservationId = UUID.randomUUID().toString();
        this.event = event;
        this.category = category;
        this.quantity = quantity;
        this.reservationDate = LocalDateTime.now();
        this.status = "CONFIRMED";
        this.userId = userId;
    }

    public String generateReservationNumber() {
        // Format: First 8 characters of reservationId
        return reservationId.substring(0, 8).toUpperCase();
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
    public String getReservationId() {
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

    public String getUserId() {
        return userId;
    }

    public double getTotalPrice() {
        return category.getPrice() * quantity;
    }
} 
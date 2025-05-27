package com.ticketsystem.model;

import java.util.UUID;

/**
 * Represents a ticket category with its properties and operations.
 */
public class TicketCategory {
    private UUID categoryId;
    private Event event;
    private String name;
    private double price;
    private int availableTickets;

    public TicketCategory(String name, Event event, double price, int availableTickets) {
        this.categoryId = UUID.randomUUID();
        this.event = event;
        this.name = name;
        this.price = price;
        this.availableTickets = availableTickets;
    }

    public TicketCategory(UUID categoryId, String name, double price) {
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
    }

    public TicketCategory(UUID categoryId, String name, double price, int availableTickets) {
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
        this.availableTickets = availableTickets;
    }

    public void updatePrice(double newPrice) {
        if (newPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = newPrice;
    }

    public void updateAvailability(int tickets) {
        if (tickets < 0) {
            throw new IllegalArgumentException("Available tickets cannot be negative");
        }
        this.availableTickets = tickets;
    }

    public boolean reserveTickets(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantity > availableTickets) {
            return false;
        }
        availableTickets -= quantity;
        return true;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    // Getters and Setters
    public UUID getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public int getAvailableTickets() {
        return availableTickets;
    }
} 
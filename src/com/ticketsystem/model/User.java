package com.ticketsystem.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a user in the ticket reservation system.
 */
public class User {
    private String userId;
    private String username;
    private String password;
    private List<Reservation> reservations;

    public User(String username, String password) {
        this.userId = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
        this.reservations = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public List<Reservation> getReservations() {
        return new ArrayList<>(reservations);
    }

    public List<Reservation> getCurrentReservations() {
        return reservations.stream()
                .filter(reservation -> !reservation.getEvent().getDate().isBefore(java.time.LocalDateTime.now()))
                .toList();
    }

    public List<Reservation> getPastReservations() {
        return reservations.stream()
                .filter(reservation -> reservation.getEvent().getDate().isBefore(java.time.LocalDateTime.now()))
                .toList();
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean verifyPassword(String password) {
        return this.password.equals(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }
} 
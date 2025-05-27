package com.ticketsystem.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a user in the ticket reservation system.
 */
public class User {
    private UUID userId;
    private String username;
    private String password;
    private String role;
    private List<Reservation> reservations;

    public User(String username, String password, String role) {
        this.userId = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.role = role;
        this.reservations = new ArrayList<>();
    }

    public User(UUID userId, String username, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
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
    public UUID getUserId() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String toString() {
        return userId + "," + username + "," + role;
    }
} 
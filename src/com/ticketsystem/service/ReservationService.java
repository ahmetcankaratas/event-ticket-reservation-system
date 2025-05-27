package com.ticketsystem.service;

import com.ticketsystem.model.Event;
import com.ticketsystem.model.Reservation;
import com.ticketsystem.model.TicketCategory;
import com.ticketsystem.repository.ReservationRepository;
import com.ticketsystem.repository.TicketCategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class to handle reservation-related operations.
 */
public class ReservationService {
    private final EventService eventService;
    private final ReservationRepository reservationRepository;
    private final TicketCategoryRepository ticketCategoryRepository;

    public ReservationService(EventService eventService, ReservationRepository reservationRepository, TicketCategoryRepository ticketCategoryRepository) {
        this.eventService = eventService;
        this.reservationRepository = reservationRepository;
        this.ticketCategoryRepository = ticketCategoryRepository;
    }

    public Reservation createReservation(UUID eventId, UUID categoryId, int quantity, UUID userId) {
        Event event = eventService.getEventById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }

        Optional<TicketCategory> category = ticketCategoryRepository.findById(categoryId);

        if (category.isEmpty()) {
            throw new IllegalArgumentException("Ticket category not found");
        }

        if (!category.get().reserveTickets(quantity)) {
            throw new IllegalArgumentException("Not enough tickets available");
        }

        Reservation reservation = new Reservation(event, category.get(), quantity, userId);
        reservationRepository.save(reservation);
        ticketCategoryRepository.update(categoryId, category.get());
        return reservation;
    }

    public Reservation getReservationById(UUID reservationId) {
        return reservationRepository.findById(reservationId).orElse(null);
    }

    public List<Reservation> getReservationsByUserId(UUID userId) {
        return reservationRepository.findAllByUser(userId);
    }

    public void cancelReservation(UUID reservationId) {
        Reservation reservation = getReservationById(reservationId);
        if (reservation == null) {
            return;
        }

        reservation.cancelReservation();
        reservationRepository.update(reservationId, reservation);
        ticketCategoryRepository.update(reservation.getCategory().getCategoryId(), reservation.getCategory());
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
} 
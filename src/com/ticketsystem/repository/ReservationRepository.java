package com.ticketsystem.repository;

import com.ticketsystem.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ReservationRepository implements IReservationRepository<Reservation, UUID>{
    private final Connection connection;
    private final UserRepository userRepository;

    public ReservationRepository(Connection connection, UserRepository userRepository) {
        this.connection = connection;
        this.userRepository = userRepository;
    }

    @Override
    public void save(Reservation obj) {
        String sql = "INSERT INTO reservations (reservation_id, event_id, category_id, quantity, reservation_date, status, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setObject(1, obj.getReservationId());
                stmt.setObject(2, obj.getEvent().getEventId());
                stmt.setObject(3, obj.getCategory().getCategoryId());
                stmt.setInt(4, obj.getQuantity());
                stmt.setTimestamp(5, Timestamp.valueOf(obj.getReservationDate()));
                stmt.setString(6, obj.getStatus());
                stmt.setObject(7, obj.getUserId());
                stmt.executeUpdate();
                connection.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Reservation> findById(UUID s) {
        String sql = """
        SELECT r.reservation_id, r.quantity, r.reservation_date, r.status, r.user_id,
               e.event_id, e.name AS event_name, e.event_date AS event_date, e.location, e.event_type, e.organizer_id,
               tc.category_id, tc.name AS category_name, tc.price
        FROM reservations r
        JOIN events e ON r.event_id = e.event_id
        JOIN ticket_categories tc ON r.category_id = tc.category_id
        WHERE r.reservation_id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, s);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UUID organizerId = rs.getObject("organizer_id", UUID.class);
                Optional<User> organizerOpt = userRepository.findById(organizerId);

                Event event = new Event(
                        rs.getObject("event_id", UUID.class),
                        rs.getString("event_name"),
                        rs.getTimestamp("event_date").toLocalDateTime(),
                        rs.getString("location"),
                        EventType.valueOf(rs.getString("event_type")),
                        organizerOpt.orElse(null)
                );

                TicketCategory category = new TicketCategory(
                        rs.getObject("category_id", UUID.class),
                        rs.getString("category_name"),
                        rs.getDouble("price"),
                        rs.getInt("available_tickets")
                );

                Reservation reservation = new Reservation(
                        rs.getObject("reservation_id", UUID.class),
                        event,
                        category,
                        rs.getInt("quantity"),
                        rs.getTimestamp("reservation_date").toLocalDateTime(),
                        rs.getString("status"),
                        rs.getObject("user_id", UUID.class)
                );
                return Optional.of(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Reservation> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(UUID s) {
        String sql = "DELETE FROM reservations WHERE reservation_id = ?";
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setObject(1, s);
                stmt.executeUpdate();
                connection.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(UUID s, Reservation newObj) {
        String sql = "UPDATE reservations SET " +
                "event_id = ?, " +
                "category_id = ?, " +
                "quantity = ?, " +
                "reservation_date = ?, " +
                "status = ?, " +
                "user_id = ? " +
                "WHERE reservation_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, newObj.getEvent().getEventId());
            stmt.setObject(2, newObj.getCategory().getCategoryId());
            stmt.setInt(3, newObj.getQuantity());
            stmt.setTimestamp(4, Timestamp.valueOf(newObj.getReservationDate()));
            stmt.setString(5, newObj.getStatus());
            if (newObj.getUserId() != null) {
                stmt.setObject(6, newObj.getUserId());
            } else {
                stmt.setNull(6, Types.OTHER); // PostgreSQL UUID için
            }
            stmt.setObject(7, s); // reservation_id (WHERE koşulu)

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new RuntimeException("No reservation found with ID: " + s);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update reservation with ID: " + s, e);
        }
    }

    @Override
    public List<Reservation> findAllByUser(UUID userId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = """
                SELECT r.reservation_id, r.quantity, r.reservation_date, r.status, r.user_id,
               e.event_id, e.name AS event_name, e.event_date AS event_date, e.location, e.event_type, e.organizer_id,
               tc.category_id, tc.name AS category_name, tc.price
        FROM reservations r
        JOIN events e ON r.event_id = e.event_id
        JOIN ticket_categories tc ON r.category_id = tc.category_id
        WHERE r.user_id = ?""";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UUID organizerId = rs.getObject("organizer_id", UUID.class);
                Optional<User> organizerOpt = userRepository.findById(organizerId);

                Event event = new Event(
                        rs.getObject("event_id", UUID.class),
                        rs.getString("event_name"),
                        rs.getTimestamp("event_date").toLocalDateTime(),
                        rs.getString("location"),
                        EventType.valueOf(rs.getString("event_type")),
                        organizerOpt.orElse(null)
                );


                TicketCategory category = new TicketCategory(
                        rs.getObject("category_id", UUID.class),
                        rs.getString("category_name"),
                        rs.getDouble("price")
                );

                Reservation reservation = new Reservation(
                        rs.getObject("reservation_id", UUID.class),
                        event,
                        category,
                        rs.getInt("quantity"),
                        rs.getTimestamp("reservation_date").toLocalDateTime(),
                        rs.getString("status"),
                        userId
                );

                reservations.add(reservation);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return reservations;
    }
}

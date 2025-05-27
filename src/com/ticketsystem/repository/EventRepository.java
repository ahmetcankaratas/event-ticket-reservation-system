package com.ticketsystem.repository;

import com.ticketsystem.model.Event;
import com.ticketsystem.model.EventType;
import com.ticketsystem.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EventRepository implements IEventRepository<Event, UUID> {
    private final Connection connection;
    private final UserRepository userRepository;

    public EventRepository(Connection connection, UserRepository userRepository) {
        this.connection = connection;
        this.userRepository = userRepository;
    }

    @Override
    public void save(Event obj) {
        String sql = "INSERT INTO events (event_id, name, event_date, location, organizer_id, event_type) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setObject(1, obj.getEventId());
                stmt.setString(2, obj.getName());
                stmt.setTimestamp(3, Timestamp.valueOf(obj.getDate()));
                stmt.setString(4, obj.getLocation());
                stmt.setObject(5, obj.getOrganizer().getUserId());
                stmt.setString(6, obj.getType().name());
                stmt.executeUpdate();
                connection.commit();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Event> findById(UUID s) {
        String sql = "SELECT * FROM events WHERE event_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, s);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UUID organizerId = rs.getObject("organizer_id", UUID.class);
                Optional<User> organizerOpt = userRepository.findById(organizerId);
                if (organizerOpt.isEmpty()) {
                    return Optional.empty();
                }
                Event event = new Event(
                        rs.getObject("event_id", UUID.class),
                        rs.getString("name"),
                        rs.getTimestamp("event_date").toLocalDateTime(),
                        rs.getString("location"),
                        EventType.valueOf(rs.getString("event_type")),
                        organizerOpt.get()
                );
                return Optional.of(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Event> findAll() {
        String sql = "SELECT * FROM events";
        List<Event> events = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UUID organizerId = rs.getObject("organizer_id", UUID.class);
                Optional<User> organizerOpt = userRepository.findById(organizerId);
                if (organizerOpt.isEmpty()) {

                }
                Event event = new Event(
                        rs.getString("name"),
                        rs.getTimestamp("event_date").toLocalDateTime(),
                        rs.getString("location"),
                        EventType.valueOf(rs.getString("event_type")),
                        organizerOpt.get()
                );
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public void deleteById(UUID s) {
        String sql = "DELETE FROM events WHERE event_id = ?";
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
    public void update(UUID s, Event newObj) {
        String sql = "UPDATE events SET name = ?, event_date = ?, location = ?, event_type = ? WHERE event_id = ?";
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, newObj.getName());
                stmt.setTimestamp(2, Timestamp.valueOf(newObj.getDate()));
                stmt.setString(3, newObj.getLocation());
                stmt.setString(4, newObj.getType().name());
                stmt.setObject(5, s);
                stmt.executeUpdate();
                connection.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Event> findEventsByTimeInterval(LocalDateTime startDate, LocalDateTime endDate) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE event_date >= ? AND event_date <= ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UUID organizerId = rs.getObject("organizer_id", UUID.class);
                Optional<User> organizerOpt = userRepository.findById(organizerId);
                if (organizerOpt.isEmpty()) {

                }
                Event event = new Event(
                        rs.getObject("event_id", UUID.class),
                        rs.getString("name"),
                        rs.getTimestamp("event_date").toLocalDateTime(),
                        rs.getString("location"),
                        EventType.valueOf(rs.getString("event_type")),
                        organizerOpt.get()
                );
                events.add(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return events;
    }

    @Override
    public List<Event> findEventsByOrganizer(UUID organizerId) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE organizer_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, organizerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UUID eventId = (UUID) rs.getObject("event_id");
                String name = rs.getString("name");
                LocalDateTime eventDate = rs.getTimestamp("event_date").toLocalDateTime();
                String location = rs.getString("location");
                EventType eventType = EventType.valueOf(rs.getString("event_type"));

                Optional<User> organizerOpt = userRepository.findById(organizerId);
                if (organizerOpt.isEmpty()) {
                    continue;
                }

                Event event = new Event(eventId, name, eventDate, location, eventType, organizerOpt.get());
                events.add(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return events;
    }
}

package com.ticketsystem.repository;

import com.ticketsystem.model.TicketCategory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TicketCategoryRepository implements ITicketCategoryRepository<TicketCategory, UUID> {
    private final Connection connection;

    public TicketCategoryRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(TicketCategory obj) {
        String sql = "INSERT INTO ticket_categories (category_id, event_id, name, price, available_tickets) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, obj.getCategoryId());
            stmt.setObject(2, obj.getEvent().getEventId());
            stmt.setString(3, obj.getName());
            stmt.setDouble(4, obj.getPrice());
            stmt.setInt(5, obj.getAvailableTickets());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<TicketCategory> findById(UUID s) {
        String sql = "SELECT * FROM ticket_categories WHERE category_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, s);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TicketCategory ticketCategory = new TicketCategory(
                            rs.getObject("category_id", UUID.class),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("available_tickets")
                    );
                    return Optional.of(ticketCategory);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<TicketCategory> findAll() {
        List<TicketCategory> categories = new ArrayList<>();
        String sql = "SELECT * FROM ticket_categories";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                TicketCategory category = new TicketCategory(
                        rs.getObject("category_id", UUID.class),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("available_tickets")
                );
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    @Override
    public void deleteById(UUID s) {
        String sql = "DELETE FROM ticket_categories WHERE category_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, s);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(UUID s, TicketCategory newObj) {
        String sql = "UPDATE ticket_categories SET name = ?, price = ?, available_tickets = ? WHERE category_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newObj.getName());
            stmt.setDouble(2, newObj.getPrice());
            stmt.setInt(3, newObj.getAvailableTickets());
            stmt.setObject(4, s);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<TicketCategory> findAllByEvent(UUID eventId) {
        List<TicketCategory> categories = new ArrayList<>();
        String sql = "SELECT * FROM ticket_categories WHERE event_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TicketCategory category = new TicketCategory(
                        rs.getObject("category_id", UUID.class),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("available_tickets")
                );
                categories.add(category);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch ticket categories for event: " + eventId, e);
        }

        return categories;
    }
}

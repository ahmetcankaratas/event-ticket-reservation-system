package com.ticketsystem;

import com.ticketsystem.model.*;
import com.ticketsystem.service.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class TicketReservationSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static final EventService eventService = new EventService();
    private static final UserService userService = new UserService();
    private static final ReservationService reservationService = new ReservationService(eventService);
    private static User currentUser = null;

    public static void main(String[] args) {
        while (true) {
            printMainMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    searchEvents();
                    break;
                case 2:
                    makeReservation();
                    break;
                case 3:
                    if (currentUser == null) {
                        login();
                    } else {
                        viewUserReservations();
                    }
                    break;
                case 4:
                    if (currentUser == null) {
                        register();
                    } else {
                        logout();
                    }
                    break;
                case 5:
                    createSampleEvents(); // For testing purposes
                    break;
                case 0:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n=== Ticket Reservation System ===");
        System.out.println("1. Search Events");
        System.out.println("2. Make Reservation");
        System.out.println("3. " + (currentUser == null ? "Login" : "View My Reservations"));
        System.out.println("4. " + (currentUser == null ? "Register" : "Logout"));
        System.out.println("5. Create Sample Events (Test)");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void searchEvents() {
        System.out.println("\n=== Search Events ===");
        System.out.println("Available events:");
        
        List<Event> events = eventService.getAllEvents();
        if (events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }

        for (Event event : events) {
            System.out.println("\nEvent: " + event.getName());
            System.out.println("Date: " + event.getDate());
            System.out.println("Location: " + event.getLocation());
            System.out.println("Type: " + event.getType());
            System.out.println("Available tickets:");
            
            for (TicketCategory category : event.getAvailableTickets()) {
                System.out.printf("- %s: $%.2f (%d available)%n",
                    category.getName(),
                    category.getPrice(),
                    category.getAvailableTickets());
            }
        }
    }

    private static void makeReservation() {
        System.out.println("\n=== Make Reservation ===");
        
        List<Event> events = eventService.getAllEvents();
        if (events.isEmpty()) {
            System.out.println("No events available for reservation.");
            return;
        }

        System.out.println("Select an event (enter event number):");
        for (int i = 0; i < events.size(); i++) {
            System.out.println((i + 1) + ". " + events.get(i).getName());
        }

        int eventChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (eventChoice < 1 || eventChoice > events.size()) {
            System.out.println("Invalid event selection.");
            return;
        }

        Event selectedEvent = events.get(eventChoice - 1);
        List<TicketCategory> categories = selectedEvent.getAvailableTickets();

        System.out.println("Select ticket category (enter category number):");
        for (int i = 0; i < categories.size(); i++) {
            TicketCategory category = categories.get(i);
            System.out.printf("%d. %s - $%.2f (%d available)%n",
                i + 1,
                category.getName(),
                category.getPrice(),
                category.getAvailableTickets());
        }

        int categoryChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (categoryChoice < 1 || categoryChoice > categories.size()) {
            System.out.println("Invalid category selection.");
            return;
        }

        System.out.print("Enter number of tickets: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            Reservation reservation = reservationService.createReservation(
                selectedEvent.getEventId(),
                categories.get(categoryChoice - 1).getCategoryId(),
                quantity,
                currentUser != null ? currentUser.getUserId() : null
            );

            System.out.println("\nReservation successful!");
            System.out.println("Reservation number: " + reservation.generateReservationNumber());
            System.out.printf("Total price: $%.2f%n", reservation.getTotalPrice());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void login() {
        System.out.println("\n=== Login ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        currentUser = userService.login(username, password)
            .orElse(null);

        if (currentUser != null) {
            System.out.println("Login successful! Welcome, " + currentUser.getUsername());
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private static void register() {
        System.out.println("\n=== Register ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            currentUser = userService.registerUser(username, password);
            System.out.println("Registration successful! Welcome, " + currentUser.getUsername());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void logout() {
        currentUser = null;
        System.out.println("Logged out successfully.");
    }

    private static void viewUserReservations() {
        if (currentUser == null) {
            System.out.println("Please login first.");
            return;
        }

        System.out.println("\n=== My Reservations ===");
        List<Reservation> reservations = reservationService.getReservationsByUserId(currentUser.getUserId());

        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
            return;
        }

        for (Reservation reservation : reservations) {
            System.out.println("\nReservation number: " + reservation.generateReservationNumber());
            System.out.println("Event: " + reservation.getEvent().getName());
            System.out.println("Date: " + reservation.getEvent().getDate());
            System.out.println("Category: " + reservation.getCategory().getName());
            System.out.println("Quantity: " + reservation.getQuantity());
            System.out.printf("Total price: $%.2f%n", reservation.getTotalPrice());
            System.out.println("Status: " + reservation.getStatus());
        }
    }

    private static void createSampleEvents() {
        // Create a concert event
        Event concert = new Event(
            "Summer Music Festival",
            LocalDateTime.now().plusDays(30),
            "Central Park",
            EventType.CONCERT
        );
        
        concert.addTicketCategory(new TicketCategory("VIP1", "VIP", 199.99, 100));
        concert.addTicketCategory(new TicketCategory("REG1", "Regular", 99.99, 500));
        eventService.addEvent(concert);

        // Create a sports event
        Event sports = new Event(
            "Championship Finals",
            LocalDateTime.now().plusDays(15),
            "Sports Arena",
            EventType.SPORTS
        );
        
        sports.addTicketCategory(new TicketCategory("VIP2", "VIP Box", 299.99, 50));
        sports.addTicketCategory(new TicketCategory("REG2", "Regular Seat", 149.99, 1000));
        eventService.addEvent(sports);

        // Create a theatre event
        Event theatre = new Event(
            "Romeo and Juliet",
            LocalDateTime.now().plusDays(45),
            "City Theatre",
            EventType.THEATRE
        );
        
        theatre.addTicketCategory(new TicketCategory("VIP3", "Premium", 149.99, 200));
        theatre.addTicketCategory(new TicketCategory("REG3", "Standard", 79.99, 300));
        eventService.addEvent(theatre);

        System.out.println("Sample events created successfully!");
    }
} 
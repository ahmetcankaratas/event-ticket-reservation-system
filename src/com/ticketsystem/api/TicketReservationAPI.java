package com.ticketsystem.api;

import com.ticketsystem.model.*;
import com.ticketsystem.repository.EventRepository;
import com.ticketsystem.repository.ReservationRepository;
import com.ticketsystem.repository.TicketCategoryRepository;
import com.ticketsystem.repository.UserRepository;
import com.ticketsystem.service.EventService;
import com.ticketsystem.service.ReservationService;
import com.ticketsystem.service.UserService;
import com.ticketsystem.util.DatabaseConnection;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class TicketReservationAPI {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final TicketCategoryRepository ticketCategoryRepository;
    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final EventService eventService;
    private final ReservationService reservationService;

    private User currentUser = null;

    Scanner scanner = new Scanner(System.in);

    public TicketReservationAPI() {
        try {
            this.userRepository = new UserRepository(DatabaseConnection.getConnection());
            this.eventRepository = new EventRepository(DatabaseConnection.getConnection(), userRepository);
            this.ticketCategoryRepository = new TicketCategoryRepository(DatabaseConnection.getConnection());
            this.reservationRepository = new ReservationRepository(DatabaseConnection.getConnection(), userRepository);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        this.userService = new UserService(userRepository);
        this.eventService = new EventService(eventRepository);
        this.reservationService = new ReservationService(eventService, reservationRepository, ticketCategoryRepository);
    }

    private void printMainMenu() {
        System.out.println("\n=== Ticket Reservation System ===");
        System.out.println("1. Search Events");
        System.out.println("2. Make Reservation");
        System.out.println("3. Login");
        System.out.println("4. Register");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private void printMenuForUser() {
        System.out.println("=== Ticket Reservation System ===");
        System.out.println("1. Search Events");
        System.out.println("2. Make Reservation");
        System.out.println("3. View My Reservations");
        System.out.println("4. Logout");
        System.out.print("Enter your choice:");
    }

    private void printMenuForFirm() {
        System.out.println("=== Ticket Reservation System ===");
        System.out.println("1. Create Event");
        System.out.println("2. Add Ticket Category To An Event");
        System.out.println("3. Logout");
        System.out.println("Enter your choice:");
    }

    //User Operations
    private void searchEvents() {
        System.out.println("\n=== Search Events ===");

        System.out.print("Enter start date time (yyyy-MM-DD HH:mm):");
        String strStartDate = scanner.nextLine();
        LocalDateTime startDate;

        try {
            startDate = LocalDateTime.parse(strStartDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please try again.");
            return;
        }

        System.out.print("Enter end date time (yyyy-MM-DD HH:mm):");
        String strEndDate = scanner.nextLine();
        LocalDateTime endDate;

        try {
            endDate = LocalDateTime.parse(strEndDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please try again.");
            return;
        }


        System.out.println("Available events:");

        List<Event> events = eventService.searchEventsByTimeInterval(startDate, endDate);
        if (events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }

        for (Event event : events) {
            System.out.println("\nEvent: " + event.getName());
            System.out.println("Date: " + event.getDate());
            System.out.println("Location: " + event.getLocation());
            System.out.println("Type: " + event.getType());
            System.out.println();
            System.out.println("Available tickets:");

            List<TicketCategory> categoriesForEvent = ticketCategoryRepository.findAllByEvent(event.getEventId());

            for (TicketCategory category : categoriesForEvent) {
                System.out.printf("- %s: $%.2f (%d available)%n",
                        category.getName(),
                        category.getPrice(),
                        category.getAvailableTickets());
            }

            System.out.println();
        }
    }

    private void makeReservation() {
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

    private void login() {
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

    private void register() {
        System.out.println("\n=== Register ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Role (User or Firm): ");
        String role = scanner.nextLine().toUpperCase();

        try {
            currentUser = userService.registerUser(username, password, role);
            System.out.println("Registration successful! Welcome, " + currentUser.getUsername());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void logout() {
        currentUser = null;
        System.out.println("Logged out successfully.");
    }

    private void viewUserReservations() {
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

    //Firm Operations
    private void createEvent() {
        System.out.println("\n=== Create Event ===");

        System.out.print("Enter event name: ");
        String eventName = scanner.nextLine();

        System.out.print("Enter event location: ");
        String location = scanner.nextLine();

        System.out.print("Enter event date and time (yyyy-MM-dd HH:mm): ");
        String dateInput = scanner.nextLine();
        LocalDateTime eventDate;
        try {
            eventDate = LocalDateTime.parse(dateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please try again.");
            return;
        }

        System.out.println("Available event types:");
        for (EventType type : EventType.values()) {
            System.out.println("- " + type);
        }
        System.out.print("Enter event type: ");
        String eventTypeInput = scanner.nextLine().toUpperCase();
        EventType eventType;
        try {
            eventType = EventType.valueOf(eventTypeInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid event type.");
            return;
        }

        Event event = new Event(
                eventName,
                eventDate,
                location,
                eventType,
                currentUser
        );

        eventService.addEvent(event);
        System.out.println("Event created successfully!");
    }

    private void addTicketCategoryToAnEvent() {
        List<Event> events = eventService.getEventsByOrganizer(currentUser.getUserId());

        if (events.isEmpty()) {
            System.out.println("No Events Found!");
            return;
        }

        for (int i = 0; i < events.size(); i++) {
            System.out.println((i + 1) + "." + events.get(i).getName());
        }

        System.out.print("Select the event:");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > events.size()) {
            System.out.println("Invalid choice");
            return;
        }

        Event selectedEvent = events.get(choice - 1);

        System.out.print("Enter category name:");
        String categoryName = scanner.nextLine();

        System.out.print("Enter category price:");
        double price = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter available tickets:");
        int availableTickets = scanner.nextInt();
        scanner.nextLine();

        TicketCategory ticketCategory = new TicketCategory(categoryName, selectedEvent, price, availableTickets);
        ticketCategoryRepository.save(ticketCategory);
        System.out.println("Ticket category for given event created successfully.");
        
    }

    public void run() {
        while (true) {
            if (currentUser == null) {
                printMainMenu();
            }

            else if (currentUser.getRole().equals("USER")) {
                printMenuForUser();
            }

            else if (currentUser.getRole().equals("FIRM")) {
                printMenuForFirm();
            }

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (currentUser == null || currentUser.getRole().equals("USER")) {
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
                        break;
                    case 0:
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
            else if (currentUser.getRole().equals("FIRM")) {
                switch (choice) {
                    case 1:
                        createEvent();
                    case 2:
                        addTicketCategoryToAnEvent();
                        break;
                    case 3:
                        logout();
                        break;
                    case 0:
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }
}

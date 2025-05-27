package com.ticketsystem;

import com.ticketsystem.api.TicketReservationAPI;

public class TicketReservationSystem {

    public static void main(String[] args) {
        TicketReservationAPI ticketReservationAPI = new TicketReservationAPI();
        ticketReservationAPI.run();
    }
} 
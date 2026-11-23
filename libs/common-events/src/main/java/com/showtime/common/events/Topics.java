package com.showtime.common.events;

/** Kafka topic names, kept in one place so producers and consumers never drift apart. */
public final class Topics {

    public static final String SHOW_EVENTS = "show.events";
    public static final String SEAT_EVENTS = "seat.events";
    public static final String BOOKING_EVENTS = "booking.events";
    public static final String PAYMENT_EVENTS = "payment.events";
    public static final String TICKET_EVENTS = "ticket.events";

    private Topics() {
    }
}

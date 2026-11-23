package com.showtime.common.web;

/** Thrown when a request conflicts with current state (duplicate, seat already taken...). Mapped to HTTP 409. */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}

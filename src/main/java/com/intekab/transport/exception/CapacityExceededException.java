package com.intekab.transport.exception;

/**
 * Thrown when a vehicle has no free seats left for the requested booking date.
 * Mapped to HTTP 409 by {@link GlobalExceptionHandler}.
 */
public class CapacityExceededException extends RuntimeException {

    public CapacityExceededException(Long vehicleId, String bookingDate, int capacity) {
        super("Vehicle " + vehicleId + " has no free seats left for " + bookingDate
                + " (capacity: " + capacity + ")");
    }
}

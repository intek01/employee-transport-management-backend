package com.intekab.transport.exception;

/**
 * Thrown when an employee already holds an active (non-cancelled) booking on the
 * same vehicle for the same date. Mapped to HTTP 409 by {@link GlobalExceptionHandler}.
 */
public class DuplicateBookingException extends RuntimeException {

    public DuplicateBookingException(Long employeeId, Long vehicleId, String bookingDate) {
        super("Employee " + employeeId + " already has an active booking on vehicle "
                + vehicleId + " for " + bookingDate);
    }
}

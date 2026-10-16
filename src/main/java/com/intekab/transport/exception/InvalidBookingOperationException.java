package com.intekab.transport.exception;

/**
 * Thrown for a structurally valid request that conflicts with a booking's current
 * state — e.g. attempting to cancel a booking that is already {@code COMPLETED}.
 * Mapped to HTTP 409 by {@link GlobalExceptionHandler}.
 */
public class InvalidBookingOperationException extends RuntimeException {

    public InvalidBookingOperationException(String message) {
        super(message);
    }
}

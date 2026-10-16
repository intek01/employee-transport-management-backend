package com.intekab.transport.exception;

/**
 * Thrown when a booking is attempted against a vehicle that is not {@code ACTIVE},
 * or whose assigned route is not {@code ACTIVE}. Mapped to HTTP 409 by
 * {@link GlobalExceptionHandler}.
 */
public class VehicleUnavailableException extends RuntimeException {

    public VehicleUnavailableException(String message) {
        super(message);
    }
}
